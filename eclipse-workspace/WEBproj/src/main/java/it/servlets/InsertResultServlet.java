package it.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.beans.Grade;
import it.utils.DataBaseConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/InsertResults")
public class InsertResultServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
	
    /*static class NewGrade{
		public String idStudente;
		public Grade grade;
	}*/

    static class NewGrade {
        String idStudente;
        Grade grade;
        NewGrade(String id, Grade g) {
            this.idStudente = id;
            this.grade = g;
        }
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
          res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "huh.. sess dead? @InsertResultServlet.java");
          return;
        }

    	String idExam = (String) session.getAttribute("currentExamId");
    	if (idExam == null) {
    		//res.sendError(401, "huh.. @InsertResultServlet");
    		res.sendError(HttpServletResponse.SC_BAD_REQUEST, "!EXAM FAILED!");
    		return;
    	}
    	System.out.println("--- InsertResults : idExam = " + idExam);
    	
    	
        /*HttpSession session = req.getSession(false);
        if (session == null)
        	res.sendError(404, "Sex expired");
        String idExam = req.getParameter("currentExamId");*/

        /*22:39 - 23Apr
         * String idExam = req.getParameter("examId");
        if (idExam == null) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Exam not selected");
            return;
        }*/
    	
    	StringBuilder payload = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
  	        String line;
   	        while ((line = reader.readLine()) != null) {
   	            payload.append(line);    	        
   	        }
    	}
    	    String raw = payload.toString();
    	    System.out.println("--- InsertResults : raw JSON = " + raw);	

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");


        if (raw.startsWith("[") && raw.endsWith("]")) {
            raw = raw.substring(1, raw.length() - 1);
        }
        String[] entries = raw
        		  .substring(1, raw.length()-1)    // drop the [ ]
        		  .split("\\},\\{");                // splits at “},{”
        		for (int i = 0; i < entries.length; i++) {
        			entries[i] = (i==0? "{" : "{\"") + entries[i] + (i==entries.length-1? "}" : "\"}"); 
        		}	//cacciando una { } ad inizio e fine
        		
        
		//https://coderanch.com/t/777123/java/retrieving-values-json-string		//TODO sarebbe carino ma fig va implementata dependency temo?
		//https://www.baeldung.com/java-servlet-post-request-payload			@ 3.2
        /*StringBuilder payload = new StringBuilder();
		try (BufferedReader reader = req.getReader()) {
			String line;
			while ((line = reader.readLine()) != null) {
				payload.append(line);
			}
		}*/ 		
        		

        Pattern pId    = Pattern.compile("\"idStudente\"\\s*:\\s*\"([^\"]+)\"");
        Pattern pGrade = Pattern.compile("\"grade\"\\s*:\\s*\"([^\"]+)\"");

        List<NewGrade> toAdd = new ArrayList<>();
        for (String entry : entries) {
            Matcher mId    = pId.matcher(entry);
            Matcher mGrade = pGrade.matcher(entry);
            if (mId.find() && mGrade.find()) {
                String studId = mId.group(1);
                Grade  g      = parseGrade(mGrade.group(1));
                toAdd.add(new NewGrade(studId, g));
            }
        }
        /*		TODO: WHY CANT I IMPORT DEPENDECNY 	KFPWMCPFKWpfèwepofmwep
        String raw = readAll(req.getReader());
        ObjectMapper mapper = new ObjectMapper();
        List<NewGrade> toAdd = mapper.readValue( raw, mapper.getTypeFactory().constructCollectionType( List.class, NewGrade.class) );
         */


        //update the sql db:
        String sql = """
        		UPDATE results 
        		SET result=?, status='added' 
        		WHERE idExam=? AND idStudent=?
        		""";
        try (Connection c = DataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
          int examInt = Integer.parseInt(idExam);
          for (NewGrade ng : toAdd) {
        	ps.setString(1, ng.grade.toString());	//should make match between 'GRADE_[num]' e '[num]' hopefully
            ps.setInt(2, examInt);
            ps.setString(3, ng.idStudente);
            ps.addBatch();
          }
          ps.executeBatch();
          res.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
          e.printStackTrace();
          res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "database died i guess?");
        }
    }

	public static Grade parseGrade(String s) {
	    switch (s.toLowerCase()) {
	        case "absent": return Grade.ABSENT;
	        case "rejected": return Grade.REJECTED;
	        case "retried": return Grade.RETRIED;
	        case "laude": return Grade.LAUDE;
	        default:
	            try {
	                int n = Integer.parseInt(s);
	                return Grade.valueOf("GRADE_" + n);
	            } catch (Exception e) {
	                throw new IllegalArgumentException("Invalid grade: " + s);
	            }
	    }
	}
}

