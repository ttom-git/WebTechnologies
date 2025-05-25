package it.servlets;

import it.utils.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.http.HttpServletRequest;


@WebServlet("/login")
public class LoginServlet extends ThymeleafServlet {

	public static final long serialVersionUID = 1L;
	

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String tipo = request.getParameter("tipo"); // può essere either docente o studente
		// System.out.println("Tipo selezionato: " + tipo);
		String tableName = null;

		if ("docente".equals(tipo)) {
			tableName = "Lecturers";
		} else if ("studente".equals(tipo)) {
			tableName = "Students";
		} else {
			request.getRequestDispatcher("index.html").forward(request, response);
			return;
		}
		
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        

		try (Connection conn = DataBaseConnection.getConnection()) {
			String sql = "SELECT * FROM " + tableName + " WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
//            
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				HttpSession session = request.getSession();
				session.setAttribute("userType", tipo);
				session.setAttribute("name", rs.getString("name"));
				session.setAttribute("surname", rs.getString("surname"));
				session.setAttribute("email", email);

				if ("docente".equals(tipo)) {
					session.setAttribute("id", rs.getInt("idLecturer"));
					response.sendRedirect( request.getContextPath() + "/lecturersArea/ExamLoaderServlet");
				} else if ("studente".equals(tipo)) {
					session.setAttribute("idStudent", rs.getString("idStudent"));
					response.sendRedirect(request.getContextPath() + "/courseLoader");
				} else {
					ctx.setVariable("error", true);
					templateEngine.process("index.html", ctx, response.getWriter());
				}

			} else {
				ctx.setVariable("error", true);
				templateEngine.process("index.html", ctx, response.getWriter());			}
		} catch (Exception e) {
			System.out.println("Exception catched @LoginServlet.java : " + e.getMessage());
			request.getRequestDispatcher("index").forward(request, response);
		}
	}
}
