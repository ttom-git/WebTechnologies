package it.servlets;

import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/StoreExamId")
public class StoreExamIdServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // called by JS to remember the selected appello in session
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String examId = req.getParameter("examId");
        HttpSession session = req.getSession();
        session.setAttribute("currentExamId", examId);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}

