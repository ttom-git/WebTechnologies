package it.servlets.Lecturers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import it.utils.DataBaseConnection;

@WebServlet("/records")
public class RecordsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String recIdStr = req.getParameter("id");
		if (recIdStr == null) {
			resp.sendError(400, " id @RecordServlet.java");
			return;
		}

		int recId = Integer.parseInt(recIdStr);
		resp.setContentType("text/html;charset=UTF-8");
		try (Connection c = DataBaseConnection.getConnection();
			PrintWriter out = resp.getWriter()) {


			String sql = """
      				SELECT date, idExam, link 
      				FROM Records 
      				WHERE idRecords = ?
      				""";
			Timestamp date;
			int examId;
			String link;
			try (PreparedStatement ps = c.prepareStatement(sql)) {
				ps.setInt(1, recId);
				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next()) {
						resp.sendError(404, "verbale not found");
						return;
					}
					date = rs.getTimestamp("date");
					examId = rs.getInt("idExam");
					link = rs.getString("link");
				}
			}
	
	      sql = """
  				  SELECT s.idStudent, s.name, s.surname, r.result
  				  FROM Results r
  				    JOIN Students s ON s.idStudent = r.idStudent
  				  WHERE r.idExam = ? AND (r.status = 'verbalized' OR r.status = 'rejected')
  				  ORDER BY s.surname, s.name
  					""";
	      try (PreparedStatement ps = c.prepareStatement(sql)) {
	    	  	ps.setInt(1, examId);

		    	  	//render HTML
		    	out.println("<!DOCTYPE html><html><head><meta charset='utf-8'><title>Verbale</title></head><body>");
		        out.printf("<h1>Verbale #%d</h1>%n", recId);
		        out.printf("<p><strong>Appello:</strong> %d<br/><strong>Data:</strong> %s<br/>", examId, date.toString());
		        out.printf("Scarica verbale: <a href='files/%s'>%s</a></p>%n", link, link);
		        out.println("<table border='1'><tr><th>Matricola</th><th>Nome</th><th>Cognome</th><th>Voto</th></tr>");
		        try (ResultSet rs = ps.executeQuery()) {
		        	while (rs.next()) {
		        		out.printf("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td></tr>%n",
		        				rs.getInt("idStudent"),
		        				rs.getString("name"),
		        				rs.getString("surname"),
		        				rs.getString("result")
		        				);
		        	}
		        }
		        out.println("</table></body></html>");
		    }
	
	    } catch (Exception e) {
	    	throw new RuntimeException("@RecordServlet.java " + e);
	    }
  }
}
