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

import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.utils.DataBaseConnection;

/**
 * Servlet implementation class addResultServlet
 */
@WebServlet("/addResult")
public class addResultServlet extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public addResultServlet() {
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
		HttpSession session = request.getSession();
		
		String corso = request.getParameter("nomeCorso");
		String idAppello = request.getParameter("idAppello");
		String[] studenti = request.getParameterValues("idStudenti");
		String[] voti = request.getParameterValues("voti");		
		try (Connection conn = DataBaseConnection.getConnection()) {
			
			for(int i = 0; i < studenti.length; i++) {
				String sql = "UPDATE results SET result = ? WHERE idExam = ? and idStudent = ?";
				
				PreparedStatement stmt = conn.prepareStatement(sql);

	            stmt.setString(1, voti[i]);
	            stmt.setString(2, idAppello);
	            stmt.setString(3, studenti[i]);
	            
	            int rowAffected = stmt.executeUpdate();
	            
	            session.setAttribute("modifyGrade", true);
	            
			}

			session.setAttribute("corso", corso);
			session.setAttribute("appello", idAppello);
			
			response.sendRedirect(request.getContextPath() + "/lecturersArea/appelliLoaderServlet");
			
		}
		catch (Exception e) {
			System.out.println("Exception catched @addResultServlet.java : " + e.getMessage());
			request.getRequestDispatcher("index").forward(request, response);
		}

		
	}

}
