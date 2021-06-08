package tech.mathai.app.Filter;


import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "ApiAccessFliter",urlPatterns = "/*")
public class ApiAccessFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        long start = System.currentTimeMillis();
        filterChain.doFilter(servletRequest, servletResponse);
        //log.info("duration: {}ms",System.currentTimeMillis()-start);
    }

    @Override
    public void destroy() {

    }
}
