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
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.beans.Appelli;
import it.beans.ExamResult;
import it.utils.DataBaseConnection;
/**
 * Servlet implementation class appelliLoaderServlet
 */
@WebServlet("/lecturersArea/appelliLoaderServlet")
public class appelliLoaderServlet extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public appelliLoaderServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		String corso = session.getAttribute("corso").toString();
		String idAppello = session.getAttribute("appello").toString();
		
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
			
			session.setAttribute("risultati", results);			
			session.setAttribute("anyResult", anyResult);
			
			
			JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
	        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
	        
	        ctx.setVariable("risultati", results);
	        ctx.setVariable("anyResult", anyResult);
	        
	        ctx.setVariable("nomeCorso", corso);
	        ctx.setVariable("idAppello", idAppello);
	        
	        
	        templateEngine.process("lecturersArea/lecturer", ctx, response.getWriter());
			
		}
		catch (Exception e) {
			System.out.println("Exception catched @appelliLoaderServlet.java : " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/index.html?error=1");
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		boolean found;
		if(session.getAttribute("exams") == null)
			response.sendRedirect(request.getContextPath() + "/lecturer.html");
		
		String corso = request.getParameter("corso").toString();
		
		if(request.getParameter("appello") == null) {
		
			try(Connection conn = DataBaseConnection.getConnection()){
				
				String query = "SELECT * FROM exams NAtURAL JOIN courses WHERE name = '"+ corso + "';";
				
				
				PreparedStatement stmt = conn.prepareStatement(query);
				
				System.out.print(query);

	
				ResultSet rs = stmt.executeQuery();
				
				if(rs == null)
					found = false;
				else {
					found = true;					
					ArrayList<Appelli> appelli = new ArrayList<>();
					
					while(rs.next()) {
						
						Appelli temp = new Appelli(rs.getInt("idExam"), rs.getString("date"));
						
						appelli.add(temp);
						
					}
					
										
					JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
			        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
			        
			        ctx.setVariable("selectedCorso", corso.toLowerCase());
			        ctx.setVariable("anyAppello", found);
					ctx.setVariable("appelli", appelli);
					
					System.out.println(session.getAttribute("anyExams"));
					System.out.println(session.getAttribute("exams"));

					
					
					ctx.setVariable("anyExams", session.getAttribute("anyExams"));
					ctx.setVariable("exams", session.getAttribute("exams"));
					
					
			        templateEngine.process("lecturersArea/lecturer", ctx, response.getWriter());
					
					System.out.print("ciao");

				}
			}
			catch (Exception e) {
				System.out.println("Exception catched @appelliLoaderServlet.java : " + e.getMessage());
				response.sendRedirect(request.getContextPath() + "/index.html?error=1");
			}
		}
		else {
			//caricamento risultati relativi agli appelli
			
			String idAppello = request.getParameter("appello").toString();
			
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
				
				session.setAttribute("risultati", results);			
				session.setAttribute("anyResult", anyResult);
				
				
				JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
		        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
		        
		        ctx.setVariable("risultati", results);
		        ctx.setVariable("anyResult", anyResult);
		        
		        ctx.setVariable("nomeCorso", corso);
		        ctx.setVariable("idAppello", idAppello);
		        
		        
		        templateEngine.process("lecturersArea/examResult", ctx, response.getWriter());
		        
				
			}
			catch (Exception e) {
				System.out.println("Exception catched @appelliLoaderServlet.java : " + e.getMessage());
				response.sendRedirect(request.getContextPath() + "/index.html?error=1");
			}
			
		}
		
	}

}
