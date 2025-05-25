package it.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/lecturer.html", "/api/iscritti/*",
                         "/PublishResults", "/verbalizeResults", "/records", "/api/records"})
public class AuthenticationFilterLecturer implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        
        HttpServletRequest  req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        boolean loggedIn = (session != null && "docente".equals(session.getAttribute("userType")) && session.getAttribute("idLecturer") != null);

    	System.out.println("---- " + session.getAttribute("userType") + " " + session.getAttribute("idStudent") + session.getAttribute("idLecturer") + " " + loggedIn);

        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
        	System.out.println("@ AuthenticationFilterLecturer - redirected back to login");
            res.sendRedirect(req.getContextPath() + "/index.html");
        }
    }
}
