package hr.bmestric.gsmshop.security;

import hr.bmestric.gsmshop.service.AsyncNotificationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AsyncNotificationService asyncNotificationService;

    public LoginSuccessHandler(AsyncNotificationService asyncNotificationService) {
        this.asyncNotificationService = asyncNotificationService;
        setDefaultTargetUrl("/");
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        String email = authentication.getName();
        String ip = resolveIp(request);
        String userAgent = request.getHeader("User-Agent");

        asyncNotificationService.recordLogin(email, ip, userAgent);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        String ip = (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
        return ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) ? "127.0.0.1" : ip;
    }
}
