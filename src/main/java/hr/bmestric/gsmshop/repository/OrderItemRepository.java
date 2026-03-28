package hr.bmestric.gsmshop.repository;

import hr.bmestric.gsmshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
