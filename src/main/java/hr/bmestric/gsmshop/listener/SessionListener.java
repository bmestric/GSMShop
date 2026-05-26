package hr.bmestric.gsmshop.listener;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class SessionListener implements HttpSessionListener {

    private static final AtomicInteger activeSessions = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        int count = activeSessions.incrementAndGet();
        log.debug("Session created: {}. Active sessions: {}", se.getSession().getId(), count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        int count = activeSessions.decrementAndGet();
        log.debug("Session destroyed: {}. Active sessions: {}", se.getSession().getId(), count);
    }

    public static int getActiveSessions() {
        return activeSessions.get();
    }
}
