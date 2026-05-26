package hr.bmestric.gsmshop.repository;

import hr.bmestric.gsmshop.entity.OrderItem;
import hr.bmestric.gsmshop.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByProductIdAndOrder_StatusNot(Long productId, OrderStatus status);
}
