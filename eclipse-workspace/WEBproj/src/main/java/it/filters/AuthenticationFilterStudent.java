package it.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/student.html", "/api/refuseGrade", "/api/StudentDataServlet", "/api/results"})
public class AuthenticationFilterStudent implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        
        HttpServletRequest  req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        boolean loggedIn = (session != null && "studente".equals(session.getAttribute("userType")) && session.getAttribute("idStudent") != null);

    	System.out.println("---- " + session.getAttribute("userType") + " " + session.getAttribute("idStudent") + session.getAttribute("idLecturer") + " " + loggedIn);

        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
        	System.out.println("@ AuthenticationFilterStudent - redirected back to login");
            res.sendRedirect(req.getContextPath() + "/index.html");
          
        }
    }
}
