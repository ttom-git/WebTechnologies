package it.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.utils.*;

import java.io.File;  // Import the File class
import java.io.FileWriter;
import java.io.IOException;

/**
 * Servlet implementation class createVerbaleServlet
 */
@WebServlet("/createVerbale")
public class createVerbaleServlet extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public createVerbaleServlet() {
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
		
		String corso = request.getParameter("courseName");
		String idAppello = request.getParameter("idExam");
		
		String[] studenti = request.getParameterValues("idStudenti");
		
		try (Connection conn = DataBaseConnection.getConnection()){
			
			String modifyReject = "UPDATE results SET result = 'retried' WHERE status = 'rejected' AND idExam = ?";
			
			PreparedStatement stmt = conn.prepareStatement(modifyReject);

            stmt.setString(1, idAppello);
            
            stmt.executeUpdate();
            
            String verbalizedGrade ="UPDATE results SET status = 'verbalized' WHERE idExam = ?";
            
            PreparedStatement stmt1 = conn.prepareStatement(verbalizedGrade);

            stmt1.setString(1, idAppello);
            
            stmt1.executeUpdate();
            
            String linkVerbale = request.getContextPath() +"/file/verbaleEsame" + idAppello;
            Date now = Date.valueOf(LocalDate.now());
            
            String insertVerbale = "INSERT INTO records(Date,IdExam,link) VALUES (?, ? , ?)";
            
            PreparedStatement stmt2 = conn.prepareStatement(insertVerbale); 
            
            stmt2.setDate(1, now);
            stmt2.setString(2, idAppello);
            stmt2.setString(3, linkVerbale);
                        
            stmt2.executeUpdate();
            
            
            JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
	        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
	       
	        ctx.setVariable("nomeCorso", corso);
	        ctx.setVariable("idAppello", idAppello);
	        
	        
	        templateEngine.process("lecturersArea/examResult", ctx, response.getWriter());

		}
		catch (Exception e) {
			System.out.println("Exception catched @LoginServlet.java : " + e.getMessage());
			request.getRequestDispatcher("index").forward(request, response);
		}
		
	}

}
