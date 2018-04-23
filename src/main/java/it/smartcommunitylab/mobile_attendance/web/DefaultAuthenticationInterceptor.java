package it.smartcommunitylab.mobile_attendance.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
@Profile("no-sec")
public class DefaultAuthenticationInterceptor extends HandlerInterceptorAdapter {

    private static final String DEFAULT_USER = "default_user";

    // use a default user
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(DEFAULT_USER, null));
        return super.preHandle(request, response, handler);
    }
}
