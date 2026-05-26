package hr.bmestric.gsmshop.service;

import hr.bmestric.gsmshop.entity.AppUser;
import hr.bmestric.gsmshop.entity.LoginHistory;
import hr.bmestric.gsmshop.repository.AppUserRepository;
import hr.bmestric.gsmshop.repository.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncNotificationService {

    private final AppUserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    @Async
    public void sendOrderConfirmation(Long orderId, String userEmail, BigDecimal total) {
        log.info("[ORDER CONFIRMATION] Order #{} placed by {} | Total: €{} | Thread: {}",
                orderId, userEmail, total, Thread.currentThread().getName());
    }

    @Async
    public void recordLogin(String email, String ip, String userAgent) {
        userRepository.findByEmail(email).ifPresent(user -> {
            LoginHistory history = new LoginHistory();
            history.setUser(user);
            history.setLoginTime(LocalDateTime.now());
            history.setIpAddress(ip);
            history.setUserAgent(userAgent);
            loginHistoryRepository.save(history);
            log.debug("[LOGIN] Recorded login for {} from {}", email, ip);
        });
    }
}
