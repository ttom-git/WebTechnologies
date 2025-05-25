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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.beans.ExamResult;
import it.beans.Grade;
import it.utils.DataBaseConnection;

/**
 * Servlet implementation class pendingGradeLoaderServlet
 */
@WebServlet("/pendingGradeLoader")
public class pendingGradeLoaderServlet extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public pendingGradeLoaderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.print("ciao");
		HttpSession session = request.getSession();
		
		String action = request.getParameter("azione");
		
		String corso = request.getParameter("courseName");
		String idAppello = request.getParameter("idExam");
		
		try (Connection conn = DataBaseConnection.getConnection()) {
			if(action.equals("addGrades")) {
				String sql = "SELECT * FROM results NATURAL JOIN students NATURAL JOIN exams WHERE idExam = ? AND (status = 'pending' OR status = 'added');";
				
				PreparedStatement stmt = conn.prepareStatement(sql);
				
	            stmt.setString(1, idAppello);
	            
	            System.out.println(stmt);
	            
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
		        ctx.setVariable("anyModifyResult", anyResult);
		        
		        ctx.setVariable("nomeCorso", corso);
		        ctx.setVariable("idAppello", idAppello);
		        
		        List<String> gradeAvailable = Arrays.stream(Grade.values())
	                    .map(Grade::toString)
	                    .sorted()
	                    .collect(Collectors.toList());
		        
		        ctx.setVariable("allGrades", gradeAvailable);
		        
		        templateEngine.process("lecturersArea/addResult", ctx, response.getWriter());
			}
			else if(action.equals("publish")) {
				String sql = "UPDATE results SET status = 'published' WHERE status = 'added'";
				
				PreparedStatement stmt = conn.prepareStatement(sql);
				
				stmt.executeUpdate();
				
				session.setAttribute("idCorso", corso);
				session.setAttribute("idAppello", idAppello);
				
				JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
		        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
		        
				
		        ctx.setVariable("risultati", session.getAttribute("risultati"));
		        ctx.setVariable("anyResult", session.getAttribute("anyResult"));
		        
		        response.sendRedirect("lecturersArea/ExamLoaderServlet");
		        
			}
			else if(action.equals("verbalizza"))
				request.getRequestDispatcher("/createVerbale").forward(request, response);
		}
		catch (Exception e) {
			System.out.println("Exception catched @LoginServlet.java : " + e.getMessage());
			request.getRequestDispatcher("index").forward(request, response);
		}
	}

}
