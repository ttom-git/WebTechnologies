package it;

import java.io.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String error = "wrong email or password";
        
        //login hardcoded until sql
        if (email.equals("docente@polimi.it") && password.equals("tette")) {
            HttpSession session = request.getSession();
            session.setAttribute("utente", "docente");
            response.sendRedirect("lecturer.html");
        } else {
            response.sendRedirect("index.html");
        }
    }
}
