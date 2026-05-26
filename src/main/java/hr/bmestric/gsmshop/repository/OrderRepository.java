package hr.bmestric.gsmshop.repository;

import hr.bmestric.gsmshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndOrderDateBetween(Long userId, LocalDateTime from, LocalDateTime to);

    List<Order> findByOrderDateBetween(LocalDateTime from, LocalDateTime to);

    List<Order> findAllByOrderByOrderDateDesc();

    List<Order> findByUser_EmailContainingIgnoreCaseOrderByOrderDateDesc(String email);

    List<Order> findByUser_EmailContainingIgnoreCaseAndOrderDateBetweenOrderByOrderDateDesc(
            String email, LocalDateTime from, LocalDateTime to);

    List<Order> findByOrderDateBetweenOrderByOrderDateDesc(LocalDateTime from, LocalDateTime to);
}
