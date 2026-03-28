package hr.bmestric.gsmshop.security;

import hr.bmestric.gsmshop.entity.AppUser;
import hr.bmestric.gsmshop.entity.LoginHistory;
import hr.bmestric.gsmshop.repository.AppUserRepository;
import hr.bmestric.gsmshop.repository.LoginHistoryRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AppUserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        String email = authentication.getName();
        AppUser user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            LoginHistory history = new LoginHistory();
            history.setUser(user);
            history.setLoginTime(LocalDateTime.now());
            history.setIpAddress(request.getRemoteAddr());
            history.setUserAgent(request.getHeader("User-Agent"));
            loginHistoryRepository.save(history);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
