package tech.mathai.app.Interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import tech.mathai.app.utils.CryptUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor extends HandlerInterceptorAdapter {



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        String token = request.getHeader("Authorization");
        HttpSession session = request.getSession(false);

        if (token != null && session != null) {
            String account = (String) session.getAttribute("account");
            if (CryptUtil.verify(token, account)) {
                return true;
            } else {
                response.sendError(401);
                return false;
            }
        } else {
            response.sendError(403);
            return false;
        }
    }
}
