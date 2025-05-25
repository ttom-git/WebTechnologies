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

import it.beans.Appelli;
import it.beans.Courses;
import it.utils.DataBaseConnection;

/**
 * Servlet implementation class courseLoaderServlet
 */
@WebServlet("/courseLoader")
public class courseLoaderServlet extends ThymeleafServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public courseLoaderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		String idStudent = session.getAttribute("idStudent").toString();
		
		try (Connection conn = DataBaseConnection.getConnection()){
			
			String sql = "SELECT * FROM studentsincourses NATURAL JOIN courses WHERE idStudent = ? ORDER BY name DESC";
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			
            stmt.setString(1, idStudent);
            
            ResultSet rs =stmt.executeQuery();
            
            System.out.println(stmt);
            
            boolean found = false;
            
            ArrayList<Courses> corsi = new ArrayList();
            
            while(rs.next()) {
            	found = true;
            	Courses temp = new Courses(rs.getInt("idCourse"), rs.getString("name"), rs.getString("year"), rs.getInt("idLecturer"));
            	
            	corsi.add(temp);
            }
            
            JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
            WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
            
            ctx.setVariable("anyCourse", found);
            ctx.setVariable("listaCorsi", corsi);
            
            session.setAttribute("anyCourse", found);
            session.setAttribute("listaCorsi", corsi);
            
            templateEngine.process("/studentsArea/student.html", ctx, response.getWriter());	
		}
		catch (Exception e) {
			System.out.println("Exception catched @courseLoadingServlet.java : " + e.getMessage());
			request.getRequestDispatcher("index").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		String idCorso = request.getParameter("corso");
		
		try (Connection conn = DataBaseConnection.getConnection()){
			
			String sql = "SELECT * FROM exams WHERE idCourse = ? ORDER BY date DESC";
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			
            stmt.setString(1, idCorso);
            
            System.out.println(stmt);
            
            ResultSet rs =stmt.executeQuery();
            
            boolean found = false;
            
            ArrayList<Appelli> appelli = new ArrayList();
            
            while(rs.next()) {
            	Appelli temp = new Appelli(rs.getInt("idExam"), rs.getString("date"));
            	
            	appelli.add(temp);
            	found = true;
            }
            
            JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
            WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
            
            
            ctx.setVariable("anyCourse", session.getAttribute("anyCourse"));
            ctx.setVariable("listaCorsi", session.getAttribute("listaCorsi"));
            ctx.setVariable("corsoSelezionato", idCorso);
            ctx.setVariable("anyAppelli", found);
            ctx.setVariable("listaAppelli", appelli);
            
            templateEngine.process("/studentsArea/student.html", ctx, response.getWriter());
			
		}
		catch (Exception e) {
			System.out.println("Exception catched @courseLoadingServlet.java : " + e.getMessage());
			request.getRequestDispatcher("index").forward(request, response);
		}		
	}

}
