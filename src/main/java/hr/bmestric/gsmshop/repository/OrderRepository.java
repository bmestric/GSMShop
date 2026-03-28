package hr.bmestric.gsmshop.repository;

import hr.bmestric.gsmshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndOrderDateBetween(Long userId, LocalDateTime from, LocalDateTime to);
}
