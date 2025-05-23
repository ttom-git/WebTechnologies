package it.servlets.Lecturers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.utils.DataBaseConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
@WebServlet("/verbalizeResults")
public class VerbalizeResultsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    //genero link
    private String generateLink(String examId) {
        return examId + ".txt";
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = req.getReader()) {
            String line;
            while ((line = r.readLine()) != null) 
            	sb.append(line);
        }
        String raw = sb.toString();

       String examId = raw.replaceAll(".*\"examId\"\\s*:\\s*\"?(\\d+)\"?.*", "$1");
       List<Integer> studs = new ArrayList<>();
       Matcher m = Pattern.compile("\"students\"\\s*:\\s*\\[(.*?)\\]").matcher(raw);
       if (m.find()) {
            for (String s : m.group(1).split(",")) {
                String n = s.replaceAll("[^0-9]", "");
                if (!n.isEmpty()) studs.add(Integer.parseInt(n));
            }
       }
       Connection conn = null;
       try {
    	   conn = DataBaseConnection.getConnection();
           conn.setAutoCommit(false);

           String sql = """
        		    UPDATE Results
        		       SET status = CASE WHEN result IN ('absent', 'retried', 'rejected') THEN 'rejected' ELSE 'verbalized' END
        		     WHERE idExam = ? AND idStudent = ?
        		""";

           try (PreparedStatement ps = conn.prepareStatement(sql)) {
        	   int ex = Integer.parseInt(examId);
        	   for (int st : studs) {
        		   ps.setInt(1, ex);
        		   ps.setInt(2, st);
        		   ps.addBatch();
               }
        	   ps.executeBatch();
           }
            

           String link = generateLink(examId);
           String insRec = """
            				INSERT INTO Records(`date`, idExam, link) 
            				VALUES (?, ?, ?)
            				""";
           int recordId;
           try (PreparedStatement ps = conn.prepareStatement(insRec, Statement.RETURN_GENERATED_KEYS)) {
        	   ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        	   ps.setInt(2, Integer.parseInt(examId));
        	   ps.setString(3, link);
        	   ps.executeUpdate();
        	   try (ResultSet keys = ps.getGeneratedKeys()) {
        		   if (!keys.next()) {
        			   throw new SQLException("No Records ID generated");
        		   }
        		   recordId = keys.getInt(1);
        	   }
           }

            /*-----------
             |	 .TXT	|
             -----------*/
           String filesDir = getServletContext().getRealPath("/files");
           File dir       = new File(filesDir);
           if (!dir.exists()) dir.mkdirs();

           File outFile = new File(dir, link);
           try (PrintWriter fw = new PrintWriter(new OutputStreamWriter(
        		   new FileOutputStream(outFile), StandardCharsets.UTF_8))) {
        	   System.out.println("Writing file to: " + outFile.getAbsolutePath());
        	   String sel = """
            			  SELECT s.idStudent, s.name, s.surname, r.result
            			  FROM Results r
            			    JOIN Students s ON s.idStudent = r.idStudent
            			  WHERE r.idExam = ? AND r.status = 'verbalized'
            			  ORDER BY s.surname, s.name
            				""";
        	   try (PreparedStatement ps = conn.prepareStatement(sel)) {
        		   ps.setInt(1, Integer.parseInt(examId));	
        		   try (ResultSet rs = ps.executeQuery()) {
        			   while (rs.next()) {
        				   fw.printf("%d %s %s %s%n",
        						   rs.getInt("idStudent"),
        						   rs.getString("name"),
        						   rs.getString("surname"),
        						   rs.getString("result")
        						   );
        			   }
        		   }
        	   }
           }

           conn.commit();

           //=> then redicret
           resp.setContentType("application/json");
           resp.setCharacterEncoding("UTF-8");
           String url = req.getContextPath() + "/records?id=" + recordId;
           resp.getWriter().write("{\"url\":\"" + url + "\"}");
       } catch (Exception e) {
    	e.printStackTrace();
       }

    }
}
