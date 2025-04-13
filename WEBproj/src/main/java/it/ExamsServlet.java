import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/exams")
public class ExamsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String corsoId = request.getParameter("corsoId");

        List<Map<String, String>> appelli = new ArrayList<>();

        appelli.add(Map.of("id", "a1", "corsoId", corsoId, "data", "2025-04-12"));
        appelli.add(Map.of("id", "a2", "corsoId", corsoId, "data", "2025-03-05"));

        appelli.sort((a, b) -> b.get("data").compareTo(a.get("data")));

        response.setContentType("application/json");
    }
}
