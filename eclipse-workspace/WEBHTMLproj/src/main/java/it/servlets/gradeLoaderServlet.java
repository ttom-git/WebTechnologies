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
import it.beans.*;

/**
 * Servlet implementation class gradeLoaderServlet
 */
@WebServlet("/gradeLoader")
public class gradeLoaderServlet extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public gradeLoaderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		String idCorso = request.getParameter("corso");
		String idAppello = request.getParameter("appello");
		String nomeCorso = null;
		
		try (Connection conn = DataBaseConnection.getConnection()){
			
			String sql = "SELECT students.name AS sname, students.surname, students.email, results.*, exams.*, c.* FROM (students NATURAL JOIN results NATURAL JOIN exams) INNER JOIN courses c ON c.idCourse = exams.idCourse WHERE c.idCourse = ? AND idExam = ?  AND idStudent = ? AND (status = 'published' OR status = 'rejected' OR status = 'verbalized');";
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			
            stmt.setString(1, idCorso);
            stmt.setString(2, idAppello);
            stmt.setString(3, session.getAttribute("idStudent").toString());
            
			ResultSet rs = stmt.executeQuery();
			
			boolean found = false;
			
			ArrayList<ExamResult> risultato = new ArrayList();
			
			while(rs.next()) {
				found = true;
				nomeCorso = rs.getString("name");
				ExamResult temp = new ExamResult(Integer.parseInt(idAppello), nomeCorso, rs.getString("date"), rs.getString("result"), rs.getString("status"), rs.getInt("idStudent"), rs.getString("sname"), rs.getString("surname"), rs.getString("email"));
				
				risultato.add(temp);
			}
			
			JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
	        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
	        
	        session.setAttribute("nomeCorso", nomeCorso);
	        session.setAttribute("idAppello", idAppello);
	        
	        ctx.setVariable("risultato", risultato);
	        ctx.setVariable("anyGrade", found);
	        
	        ctx.setVariable("nomeCorso", nomeCorso);
	        ctx.setVariable("idAppello", idAppello);
	        
	        templateEngine.process("studentsArea/viewResult", ctx, response.getWriter());	        
	        
		}
		catch (Exception e) {
			System.out.println("Exception catched @courseLoadingServlet.java : " + e.getMessage());
			request.getRequestDispatcher("index").forward(request, response);
		}
	}

}
