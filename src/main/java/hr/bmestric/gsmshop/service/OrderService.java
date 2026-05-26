package hr.bmestric.gsmshop.service;

import hr.bmestric.gsmshop.dto.Cart;
import hr.bmestric.gsmshop.dto.DeliveryInfo;
import hr.bmestric.gsmshop.entity.Order;
import hr.bmestric.gsmshop.enums.OrderStatus;
import hr.bmestric.gsmshop.enums.PaymentMethod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    Order createOrder(String userEmail, Cart cart, PaymentMethod paymentMethod,
                      String paypalTransactionId, DeliveryInfo delivery);

    List<Order> findByUserEmail(String userEmail);

    Optional<Order> findById(Long id);

    void updateStatus(Long orderId, OrderStatus status);

    List<Order> findOrders(String customerEmail, LocalDate dateFrom, LocalDate dateTo);
}
