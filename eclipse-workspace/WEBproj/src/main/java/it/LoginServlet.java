package it;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import it.utils.DataBaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String tipo = request.getParameter("tipo"); // può essere either docente o studente
        //System.out.println("Tipo selezionato: " + tipo);
        String tableName = null;

        if ("docente".equals(tipo)) {
            tableName = "Lecturers";
        } else if ("studente".equals(tipo)) {
            tableName = "Students";
        } else {
            response.sendRedirect("index.html?error=1");
            return;
        }

        try (Connection conn = DataBaseConnection.getConnection()) {
            String sql = "SELECT * FROM " + tableName + " WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("userType", tipo);
                session.setAttribute("name", rs.getString("name"));
                session.setAttribute("surname", rs.getString("surname"));                
                session.setAttribute("email",  email);
                	
                
                
                if ("docente".equals(tipo)) {
                    response.sendRedirect("lecturer.html");
                } else if ("studente".equals(tipo)){
                    response.sendRedirect("student.html");
                } else {
                    response.sendRedirect("index.html?error=2");
                }
             
            } else {
                response.sendRedirect("index.html?error=1");
            }
        } catch (Exception e) {
        	System.out.println("Exception catched @LoginServlet.java : " + e.getMessage());
            response.sendRedirect("index.html?error=1");
        }
    }
}
