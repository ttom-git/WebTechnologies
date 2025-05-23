package it.servlets.Login;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.mindrot.jbcrypt.BCrypt;

import it.utils.DataBaseConnection;

@WebServlet("/changePassword")
public class ChangePasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // numero round per BCrypt : https://it.wikipedia.org/wiki/Bcrypt
    //https://stackoverflow.com/questions/46693430/what-are-salt-rounds-and-how-are-salts-stored-in-bcrypt
    private static final int WORKLOAD = 12;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email        = request.getParameter("email");
        String tipo         = request.getParameter("tipo");        // "docente" | "studente"
        String newPassword  = request.getParameter("newPassword");

        if (email == null || tipo == null || newPassword == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "smth missing");
            return;
        }
        //picka tab giusta
        String table = "docente".equals(tipo) ? "Lecturers"
                     : "studente".equals(tipo) ? "Students"
                     : null;
        if (table == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                "Tipo utente non valido");
            return;
        }

        // genera l’hash BCrypt
        String salt = BCrypt.gensalt(WORKLOAD);
        String hashed = BCrypt.hashpw(newPassword, salt);
        System.out.println("new hash della password: " + hashed);

        String sql = "UPDATE " + table + " SET password = ? WHERE email = ?";

        try (Connection conn = DataBaseConnection.getConnection()){
        	PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, hashed);
            ps.setString(2, email);
            int updated = ps.executeUpdate();
            if (updated != 1) {
                // nessuna riga aggiornata -> email non trovata (fa anche rima lmao)
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "utente non trovato");
                return;
            }
            // OK: restituisci 200
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "rrrore @ChangePasswordServlet.java :)");
        }
    }
}
