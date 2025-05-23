package it.servlets.Students;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import it.utils.DataBaseConnection;

@WebServlet("/api/results")
public class StudentsResultsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("idStudent") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String idStudent = (String) session.getAttribute("idStudent");
        String idExam    = req.getParameter("idExam");
        if (idExam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing idExam");
            return;
        }

        String sql = """
        		SELECT c.name AS courseName,
        				e.date AS examDate,
        		        r.result AS grade,
        		        r.status
        		FROM Results r
        				JOIN Exams e ON r.idExam = e.idExam
        		        JOIN Courses c ON e.idCourse = c.idCourse
        		WHERE r.idStudent = ? AND r.idExam = ?
        """;

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try ( Connection conn = DataBaseConnection.getConnection();		PreparedStatement ps = conn.prepareStatement(sql) ) {
           		ps.setString(1, idStudent);
           		ps.setString(2, idExam);
            	try ( ResultSet rs = ps.executeQuery();		PrintWriter out = resp.getWriter()) {
            		if (!rs.next()) {
                    // nessun risultato trovato
                    out.print("{}");
            		} else {
	                    String course = rs.getString("courseName");
	                    String date   = rs.getDate("examDate").toString();
	                    String grade  = rs.getString("grade");
	                    String status = rs.getString("status");
	
	                    // escape virgolette nel course
	                    course = course.replace("\"","\\\"");
	                    grade  = grade  == null ? "" : grade.replace("\"","\\\"");
	                    status = status == null ? "" : status.replace("\"","\\\"");
	
	                    String json = String.format(
	                      "{\"idExam\":%s,"
	                      + "\"courseName\":\"%s\","
	                      + "\"date\":\"%s\"," 
	                      +"\"grade\":\"%s\","
	                      + "\"status\":\"%s\"}",
	                      idExam, course, date, grade, status
	                    );
	                    out.print(json);
            		}
            	}

        } catch (Exception e) {
        	e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
