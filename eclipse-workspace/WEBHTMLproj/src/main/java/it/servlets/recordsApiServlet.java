package it.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.utils.DataBaseConnection;
import it.beans.Record;

/**
 * Servlet implementation class recordsApiServlet
 */
@WebServlet("/recordsApi")
public class recordsApiServlet extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public recordsApiServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String idAppello = request.getParameter("idAppello");
	    if (idAppello == null) {
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro idAppello mancante");
	        return;
	    }

	    String filesDir = getServletContext().getRealPath("/files");
	    File dir = new File(filesDir);
	    File inputFile = new File(dir, idAppello + ".txt");

	    if (!inputFile.exists()) {
	        response.sendError(HttpServletResponse.SC_NOT_FOUND, "File non trovato");
	        return;
	    }
	    
		System.out.println("Search file to: " + inputFile.getAbsolutePath());


	    // Imposta le intestazioni per forzare il download
	    response.setContentType("application/octet-stream");
	    response.setHeader("Content-Disposition", "attachment;filename=\"" + inputFile.getName() + "\"");
	    response.setContentLengthLong(inputFile.length());

	    try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));
	         BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())) {

	        byte[] buffer = new byte[4096];
	        int bytesRead;
	        while ((bytesRead = in.read(buffer)) != -1) {
	            out.write(buffer, 0, bytesRead);
	        }
	        	        
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante il download del file");
	    }
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();

		String lectId = (session != null) ? (String) session.getAttribute("id").toString() : null;

		try (Connection conn = DataBaseConnection.getConnection()) {
			String sql = """
									  SELECT r.idRecords AS id, r.idExam,
						r.date AS date,
						 		    c.name AS courseName,
						 		    e.date AS examDate,
						 		    r.link AS link
						  FROM Records r
						 	JOIN Exams e ON r.idExam = e.idExam
						  	JOIN Courses c ON e.idCourse = c.idCourse
						  WHERE c.idLecturer = ?
						 	ORDER BY c.name DESC;

					""";

			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, lectId);

			ResultSet rs = stmt.executeQuery();
			
			ArrayList<Record> verbali = new ArrayList();
			
			System.out.print(stmt);
			
			boolean found = false;
			
			while(rs.next()) {
				found = true;
				Record temp = new Record(rs.getInt("id"), rs.getInt("idExam"), rs.getTimestamp("date"), rs.getString("courseName"), rs.getDate("examDate"), "/webapp/files/" + rs.getString("link"));
				verbali.add(temp);
			}
			
			JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
	        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
	        
	        session.setAttribute("anyVerbali", found);
	        session.setAttribute("verbali", verbali);
	        
	        ctx.setVariable("anyVerbali", found);
	        ctx.setVariable("verbali", verbali);
	        
	        templateEngine.process("lecturersArea/records", ctx, response.getWriter());

		} catch (Exception e) {
			System.out.println("Exception catched @recordApiServlet.java : " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/index.html?error=1");
		}
	}

}
