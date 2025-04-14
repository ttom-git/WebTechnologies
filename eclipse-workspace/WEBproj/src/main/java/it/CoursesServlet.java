package it;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/corsi")
public class CoursesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;	//idk.. eclipse made me do this

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        // Mock JSON - in futuro sostituire con query DB
        out.print("[{\"id\":1,\"nome\":\"Algoritmi\"},{\"id\":2,\"nome\":\"Basi di Dati\"}]");
        out.flush();
    }
}
