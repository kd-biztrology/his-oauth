package com.jims.his.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by heren on 2015/11/21.
 */
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request ;
        HttpSession session = httpServletRequest.getSession();
        String loginName = (String)session.getAttribute("loginName") ;
        if(loginName !=null){
            chain.doFilter(request,response);
            return ;
        }

        StringBuffer requestURL = httpServletRequest.getRequestURL();
        String path = requestURL.toString() ;
        boolean login = path.contains("login")||path.contains("assert")||path.contains("jnlp");
        if(login){
            chain.doFilter(request,response);
            return ;
        }
        System.out.println(requestURL);
        HttpServletResponse httpServletResponse = (HttpServletResponse)response ;

        httpServletResponse.sendRedirect("/login1.html") ;

        //chain.doFilter(request,response);
        return ;

    }

    @Override
    public void destroy() {

    }
}
