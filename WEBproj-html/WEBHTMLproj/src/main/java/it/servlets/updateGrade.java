package it.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.utils.DataBaseConnection;

/**
 * Servlet implementation class updateGrade
 */
@WebServlet("/updateGrade")
public class updateGrade extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("azione");
		
		String idExam = request.getParameter("idExam");
		
		try(Connection conn = DataBaseConnection.getConnection()){
			String sql = "UPDATE results SET status = ? WHERE idExam = ?";
					
			PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, action);
            stmt.setString(2, idExam);
            
            System.out.print(stmt);
            
			int modified = stmt.executeUpdate();
			
			JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
	        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
			
			if(modified == 1) {
				ctx.setVariable("succ", true);				
			}
			else if(modified == 0) {
				ctx.setVariable("succ", false);
			}
			
	        templateEngine.process("lecturersArea/examResult", ctx, response.getWriter());
			
			

		}
		catch (Exception e) {
			System.out.println("Exception catched @appelliLoaderServlet.java : " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/index.html?error=1");
		}
	}

}
