package it.smartcommunitylab.mobile_attendance.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import it.smartcommunitylab.aac.AACException;
import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.AACService;
import it.smartcommunitylab.aac.model.AccountProfile;

@Component
@Profile("sec")
public class AACAuthenticationInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger =
            LoggerFactory.getLogger(AACAuthenticationInterceptor.class);

    @Autowired
    private AACProfileService aacProfileService;

    @Autowired
    private AACService aacService;

    @Value("${aac.attendanceScope}")
    private String attendanceScope;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {


        String token = extractToken(request);
        boolean passRequest = true;
        if (token != null) {
            AccountProfile profile = aacProfileService.findAccountProfile(token);
            String email = profile.getAttribute("google", "email");

            if (!isFbkEmail(email)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "not a FBK account");
                passRequest = false;
            } else if (!hasAttendanceScope(token)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "not the right security scope");
                passRequest = false;
            } else {
                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(email, null));
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "request should be authenticated");
            passRequest = false;
        }

        return passRequest;
    }


    private String extractToken(HttpServletRequest request) {
        String authHeaderValue = request.getHeader("Authorization");
        if (authHeaderValue != null) {
            return authHeaderValue.replaceFirst("Bearer ", "");
        }

        return null;
    }

    private boolean isFbkEmail(String email) {
        return email != null && email.endsWith("@fbk.eu");
    }

    private boolean hasAttendanceScope(String token) {
        try {
            return aacService.isTokenApplicable(token, attendanceScope);
        } catch (AACException e) {
            logger.error("AACService exception: ", e);
            return false;
        }
    }
}
