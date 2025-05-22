package it.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.mindrot.jbcrypt.BCrypt;

import it.utils.DataBaseConnection;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String tipo = request.getParameter("tipo");	 // può essere either docente o studente
        System.out.println("DEBUG: parametro tipo -> '" + tipo + "'");
        //System.out.println("Tipo selezionato: " + tipo);
        String tableName = null;

        if ("docente".equals(tipo)) {
            tableName = "Lecturers";
        } else if ("studente".equals(tipo)) {
            tableName = "Students";
        } else if (tipo == null){
        	System.out.println("!!Tipo NULLO!!");
            response.sendRedirect("index.html?error=1");
            return;
        } else {
        	System.out.println("!!Tipo non trovato!!");
            response.sendRedirect("index.html?error=1");
            return;
        }

        try (Connection conn = DataBaseConnection.getConnection()) {
            /*String sql = "SELECT * FROM " + tableName + " WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery() ;
            

            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("userType", tipo);
                session.setAttribute("name", rs.getString("name"));
                session.setAttribute("surname", rs.getString("surname"));                
                session.setAttribute("email",  email);
               
                
                if ("docente".equals(tipo)) {
                    response.sendRedirect("lecturer.html");
                } else if ("studente".equals(tipo)){
                    session.setAttribute("idStudent", rs.getString("idStudent"));
                    response.sendRedirect("student.html");
                } else {
                    response.sendRedirect("index.html?error=2");
                }
             
            } else {
                response.sendRedirect("index.html?error=1");
            }
            
            */
        	
        
        	String sql = "SELECT * FROM " + tableName + " WHERE email = ?";
        	PreparedStatement stmt = conn.prepareStatement(sql);
        	stmt.setString(1, email);
        	ResultSet rs = stmt.executeQuery();

        	// verifico esista utente con quell'email
        	if (!rs.next()) {
            	System.out.println("DEBUG: EMAIL NON TROVATA");

        	    // email non trovata
        	    response.sendRedirect("index.html?error=1");
        	    return;
        	}

        	// recupero hash salvato 
        	String hashedPasswordFromDB = rs.getString("password");
        	System.out.println("DEBUG: passwordFromDB -> «" + hashedPasswordFromDB + "»");
        	
        	String raw = rs.getString("password");
        	String pwdFromDB = (raw != null ? raw.trim() : "");
        	if ("password".equals(pwdFromDB)) {
        		//else la  psw non è mai stata cambiata => redirect        	    
        		HttpSession session = request.getSession();
        	    session.setAttribute("userEmail", email);
        	    session.setAttribute("userType", tipo);
        	    response.setContentType("application/json");
        	    response.getWriter().write("{\"forcePwdChange\":true}");
        	    return;
        	}
        	else
	        	if (!BCrypt.checkpw(password, hashedPasswordFromDB)) {
	        	    // psw sbagliata
	        	    response.sendRedirect("index.html?error=1");
	        	    return;
	        	}         //else psw giusta

        		

        	// se arrivo qui utente e password OK =>alor popolo
        	HttpSession session = request.getSession();
        	session.setAttribute("userType", tipo);
        	session.setAttribute("name", rs.getString("name"));
        	session.setAttribute("surname", rs.getString("surname"));
        	session.setAttribute("email", email);
        	if ("docente".equals(tipo)) {
        	    response.sendRedirect("lecturer.html");
        	} else {
        	    session.setAttribute("idStudent", rs.getString("idStudent"));
        	    response.sendRedirect("student.html");
        	}
        } catch (Exception e) {
        	System.out.println("Exception catched @LoginServlet.java : " + e.getMessage());
            response.sendRedirect("index.html?error=1");
        }
    }
}