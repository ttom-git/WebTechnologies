package it.servlets.Lecturers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import it.utils.DataBaseConnection;

@WebServlet("/api/records")
public class RecordsApiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    HttpSession session = req.getSession(false);
	    String lecturerId = (session != null)
	    	     ? (String) session.getAttribute("idLecturer")
	    		 : null;
	
	    if (lecturerId == null) {
	      resp.sendError(401, "Not logged in");
	      return;
	    }
	
	    //passo dati in json
	    resp.setContentType("application/json");
	    resp.setCharacterEncoding("UTF-8");
	    
	    String sql = """
				  SELECT r.idRecords AS id,
							r.date AS date,
	    		 		    c.name AS courseName,
	    		 		    e.date AS examDate,
	    		 		    r.link AS link
	    		  FROM Records r
	    		 	JOIN Exams e ON r.idExam = e.idExam
	    		  	JOIN Courses c ON e.idCourse = c.idCourse	
	    		  WHERE c.idLecturer = ?
	    		 	ORDER BY c.name DESC
	    			""";
	    try (Connection c = DataBaseConnection.getConnection();	
	    	 PreparedStatement ps = c.prepareStatement(sql)) {
	    		ps.setInt(1, Integer.valueOf(lecturerId));
	    		try (ResultSet rs = ps.executeQuery()) {
	    				StringBuilder json = new StringBuilder("[");
	    				boolean first = true;
	    				while (rs.next()) {
	    					if (!first) {
	    						json.append(",");
	    						first = false;
	    					}
	    				json.append("{")
	    					.append("\"id\":").append(rs.getInt("id")).append(",")
	    					.append("\"date\":\"").append(rs.getTimestamp("date")).append("\",")
	    					.append("\"course\":\"").append(rs.getString("courseName")).append("\",")
	    					.append("\"examDate\":\"").append(rs.getDate("examDate")).append("\",")
	    					.append("\"link\":\"").append(rs.getString("link")).append("\"")
	    					.append("}");
	    				}
	    			json.append("]");
	    			try (PrintWriter out = resp.getWriter()) {
	    				out.print(json);
	    			}
	    		}
	    } catch (SQLException e) {
	    	resp.sendError(500, e.getMessage());
	    }
	}
}
