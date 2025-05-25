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

import it.beans.ExamResult;
import it.utils.DataBaseConnection;

/**
 * Servlet implementation class loadResultServlet
 */
@WebServlet("/loadResult")
public class loadResultServlet extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public loadResultServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();

		String corso = request.getParameter("corso").toString();
		String idAppello = request.getParameter("appello").toString();
		
		boolean found = false;
				
		try(Connection conn = DataBaseConnection.getConnection()){
			
			String sql = "SELECT * FROM results NATURAL JOIN students NATURAL JOIN exams WHERE idExam = ?";
			
			PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, idAppello);
            
			ResultSet rs = stmt.executeQuery();
			
			session.setAttribute("idCorso", corso);
			session.setAttribute("idAppello", idAppello);
			
			ArrayList<ExamResult> results = new ArrayList<>();
			
			boolean anyResult = false;
			
			while(rs.next()) {
				anyResult = true;
				ExamResult temp = new ExamResult(Integer.parseInt(idAppello), corso, rs.getString("date"), rs.getString("result"), rs.getString("status"), rs.getInt("idStudent"), rs.getString("name"), rs.getString("surname"), rs.getString("email"));
				results.add(temp);
			}
			
			String verbalizeCheck = "SELECT * FROM records WHERE idExam = ?";
			
			PreparedStatement query = conn.prepareStatement(verbalizeCheck);
			
			query.setString(1, idAppello);
			
			ResultSet result = query.executeQuery();
			
			boolean canVerbalize = true;
			
			while(result.next())
				canVerbalize = false;
			
			session.setAttribute("risultati", results);			
			session.setAttribute("anyResult", anyResult);
			
			session.setAttribute("canVerbalize", canVerbalize);
			
			
			JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
	        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
	        
	        ctx.setVariable("selectedCorso", corso);
	        ctx.setVariable("risultati", results);
	        ctx.setVariable("anyResult", anyResult);
	        
	        ctx.setVariable("nomeCorso", corso);
	        ctx.setVariable("idAppello", idAppello);
	        
	        ctx.setVariable("canVerbalize", canVerbalize);
	        
	        
	        templateEngine.process("lecturersArea/examResult", ctx, response.getWriter());
	        
			
		}
		catch (Exception e) {
			System.out.println("Exception catched @loadResultServlet.java : " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/index.html?error=1");
		}
		
	}

}
