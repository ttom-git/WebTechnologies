	package it.servlets;
	
	import java.io.IOException;
	import java.io.PrintWriter;
	import java.sql.Connection;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import it.beans.Courses;
import it.utils.DataBaseConnection;
	import jakarta.servlet.ServletException;
	import jakarta.servlet.annotation.WebServlet;
	import jakarta.servlet.http.HttpServlet;
	import jakarta.servlet.http.HttpServletRequest;
	import jakarta.servlet.http.HttpServletResponse;
	import jakarta.servlet.http.HttpSession;
	
	@WebServlet("/api/courses")
	public class CoursesServlet extends HttpServlet {
	    private static final long serialVersionUID = 1L;
	
	    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        PrintWriter out = response.getWriter();
	
	        HttpSession session = request.getSession(false);	        // get current session
	        if (session == null)
	        	response.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
	        switch(session.getAttribute("userType").toString()) {
	        	case "docente" -> lecturerCoursesFunct(response, session, out);
	        	
	        	case "studente" -> studentCoursesFunct(response, session, out);
	        	
	        	default -> {	            
	        		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        		out.print("[]");
	        		out.flush();
	        	}
	        }
	            
	    }
	   


		
		/*----------------
		 |	  LECTURER	 |
		 ----------------*/
		
		private void lecturerCoursesFunct(HttpServletResponse response, HttpSession session, PrintWriter out) {
				
	       
	        String email = (String) session.getAttribute("email");
	        int lecturerId = -1;
	        
	        try (Connection conn = DataBaseConnection.getConnection()) {
	            try (PreparedStatement stmt = conn.prepareStatement("""
												            		SELECT idLecturer 
												            		FROM Lecturers 
												            		WHERE email = ?
												            		"""))
	            {
	                stmt.setString(1, email);
	                try (ResultSet rs = stmt.executeQuery()) {
	                    if (rs.next()) {
	                        lecturerId = rs.getInt("idLecturer");
	                    }
	                }
	            }
	
	            if (lecturerId < 0) {                // no matching lecturer
	                out.print("[]");
	                out.flush();
	                return;
	            }
	
	            //  query courses of this lecturer, ordered by name DESC
	            List<String> coursesJson = new ArrayList<>();
	            String sql = """
	            		SELECT idCourse, name 
	            		FROM Courses 
	            		WHERE idLecturer = ? 
	            		ORDER BY name DESC
	            		""";
	            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	                stmt.setInt(1, lecturerId);
	                try (ResultSet rs = stmt.executeQuery()) {
	                    while (rs.next()) {
	                        int id = rs.getInt("idCourse");
	                        String name = rs.getString("name");
	                        coursesJson.add(String.format("{\"id\":%d,\"nome\":\"%s\"}", id, name));
	                    }
	                }
	            }
	
	            // Output
	            out.print("[" + String.join(",", coursesJson) + "]");
	        } catch (Exception e) {
	            e.printStackTrace();
	            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            out.print("[]");
	        } finally {
	            out.flush();
	        }
	        
		}
		
		
		
		
		/*----------------
		 |	  STUDENT	 |
		 ----------------*/
		
		private void studentCoursesFunct(HttpServletResponse response, HttpSession session, PrintWriter out) {
		    String idStudent = (String) session.getAttribute("idStudent");
	        List<String> coursesJson = new ArrayList<>();
	        String sql = """
	        	      SELECT c.idCourse, c.name
	        	        FROM Courses c JOIN StudentsInCourses sc ON sc.idCourse = c.idCourse
	        	        WHERE sc.idStudent = ?
	        	    """;

	        try (Connection conn = DataBaseConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	             
	            ps.setString(1, idStudent);
	            try (ResultSet rs = ps.executeQuery()) {
	                while (rs.next()) {
	                    int id   = rs.getInt("idCourse");
	                    String name = rs.getString("name")
	                                     .replace("\"","\\\""); 
	                    // escape eventual virgolette
	                    coursesJson.add( String.format("{\"idCourse\":%d,\"name\":\"%s\"}", id, name) );
	                }
	            }
	            out.print("[" + String.join(",", coursesJson) + "]");
	        } catch (Exception e) {
	        	e.printStackTrace();
	            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            out.print("[]");
	        } finally {
	            out.flush();
	        }
	    }
		
	}
