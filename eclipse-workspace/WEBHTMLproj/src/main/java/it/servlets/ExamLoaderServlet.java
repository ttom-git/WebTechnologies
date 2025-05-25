package it.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.utils.DataBaseConnection;

/**
 * Servlet implementation class ExamLoaderServlet
 */
@WebServlet("/lecturersArea/ExamLoaderServlet")
public class ExamLoaderServlet extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExamLoaderServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();

		String idLecturer = session.getAttribute("id").toString();
		

		try (Connection conn = DataBaseConnection.getConnection()) {
			
			String query = "SELECT * FROM courses WHERE idLecturer = ? ORDER BY name DESC";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, idLecturer);
			
			ResultSet rs = stmt.executeQuery();

			ArrayList<String> exams = new ArrayList<>();

			boolean found = false;
			while (rs.next()) {
			    exams.add(rs.getString("name"));
			    found = true;
			}
			
			System.out.print(exams.size());
			
			JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
	        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
	        
	        session.setAttribute("anyExams", found);
	        session.setAttribute("exams", exams);
	        
	        ctx.setVariable("anyExams", found);
			ctx.setVariable("exams", exams);
			
	        templateEngine.process("lecturersArea/lecturer", ctx, response.getWriter());

		} catch (Exception e) {
			System.out.println("Exception catched @LoginServlet.java : " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/index.html");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
			doGet(request, response);

	}

}
