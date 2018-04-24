package it.smartcommunitylab.mobile_attendance.web;

import java.io.IOException;

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

    private static enum Action {
        READ, WRITE
    };

    @Autowired
    private AACProfileService aacProfileService;

    @Autowired
    private AACService aacService;

    @Value("${aac.attendanceScopes.read}")
    private String readAttendanceScopes;

    @Value("${aac.attendanceScopes.write}")
    private String writeAttendanceScopes;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws IOException {


        String token = extractToken(request);
        boolean passRequest = true;
        if (token != null) {
            AccountProfile profile = null;
            String email = null;
            try {
                profile = aacProfileService.findAccountProfile(token);
                email = profile.getAttribute("google", "email");
            } catch (SecurityException | AACException e) {
                logger.warn("Exception in findAccountProfile of aacProfileService", e.getMessage());
            }

            if (!isFbkEmail(email)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "not a FBK account");
                logger.warn("Try to use an account not FBK: {}", email);
                passRequest = false;
            } else if (!hasAttendanceScope(getAction(request), token)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "not the right security scope");
                logger.warn("Token has not scope: {}", getAction(request) == Action.READ
                        ? readAttendanceScopes : writeAttendanceScopes);
                passRequest = false;
            } else {
                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(email, null));
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "request should be authenticated");
            logger.warn("Request without token");
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

    private boolean hasAttendanceScope(Action action, String token) {
        try {
            if (action == Action.READ) {
                return aacService.isTokenApplicable(token, readAttendanceScopes);
            } else if (action == Action.WRITE) {
                return aacService.isTokenApplicable(token, writeAttendanceScopes);
            }
            logger.warn("Unsupported action {}", action.toString());
            return false;
        } catch (AACException e) {
            logger.error("AACService exception: ", e);
            return false;
        }
    }

    private Action getAction(HttpServletRequest req) {
        if ("post".equalsIgnoreCase(req.getMethod())) {
            return Action.WRITE;
        }
        if ("get".equalsIgnoreCase(req.getMethod())) {
            return Action.READ;
        }

        logger.warn("Method not supported to reatrive the action {}", req.getMethod());
        return null;
    }

    public String getWriteAttendanceScopes() {
        return writeAttendanceScopes;
    }


    public void setWriteAttendanceScopes(String writeAttendanceScopes) {
        this.writeAttendanceScopes = writeAttendanceScopes;
    }
}
