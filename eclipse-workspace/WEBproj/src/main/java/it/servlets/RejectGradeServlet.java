package it.servlets;

import java.io.IOException;

import it.dao.ExamDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RejectGradeServlet")
public class RejectGradeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String examId = request.getParameter("idExam");
        String studentId = (String) request.getSession().getAttribute("idStudent");

        if (examId == null || studentId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        int examIdInt = Integer.parseInt(examId);
        if (ExamDAO.rejectExamGrade(studentId, examIdInt)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
