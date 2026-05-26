package hr.bmestric.gsmshop.service.impl;

import hr.bmestric.gsmshop.dto.Cart;
import hr.bmestric.gsmshop.dto.CartItem;
import hr.bmestric.gsmshop.dto.DeliveryInfo;
import hr.bmestric.gsmshop.entity.AppUser;
import hr.bmestric.gsmshop.entity.Order;
import hr.bmestric.gsmshop.entity.OrderItem;
import hr.bmestric.gsmshop.entity.Product;
import hr.bmestric.gsmshop.enums.OrderStatus;
import hr.bmestric.gsmshop.enums.PaymentMethod;
import hr.bmestric.gsmshop.repository.AppUserRepository;
import hr.bmestric.gsmshop.repository.OrderRepository;
import hr.bmestric.gsmshop.repository.ProductRepository;
import hr.bmestric.gsmshop.service.AsyncNotificationService;
import hr.bmestric.gsmshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AppUserRepository userRepository;
    private final ProductRepository productRepository;
    private final AsyncNotificationService asyncNotificationService;

    @Override
    @Transactional
    public Order createOrder(String userEmail, Cart cart, PaymentMethod paymentMethod,
                             String paypalTransactionId, DeliveryInfo delivery) {
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentMethod(paymentMethod);
        order.setPaypalTransactionId(paypalTransactionId);
        order.setTotalAmount(cart.getTotal());
        order.setStatus(paymentMethod == PaymentMethod.PAYPAL
                ? OrderStatus.COMPLETED : OrderStatus.PENDING);

        if (delivery != null) {
            order.setDeliveryAddress(delivery.getAddress());
            order.setDeliveryCity(delivery.getCity());
            order.setDeliveryPostalCode(delivery.getPostalCode());
            order.setDeliveryCountry(delivery.getCountry());
            order.setDeliveryPhone(delivery.getPhone());
        }

        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found: " + cartItem.getProductId()));

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPrice());
            order.getItems().add(orderItem);
        }

        Order saved = orderRepository.save(order);
        asyncNotificationService.sendOrderConfirmation(saved.getId(), userEmail, saved.getTotalAmount());
        return saved;
    }

    @Override
    public List<Order> findByUserEmail(String userEmail) {
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));
        return orderRepository.findByUserId(user.getId());
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findOrders(String customerEmail, LocalDate dateFrom, LocalDate dateTo) {
        boolean hasEmail = customerEmail != null && !customerEmail.isBlank();
        boolean hasDates = dateFrom != null && dateTo != null;

        if (hasEmail && hasDates) {
            return orderRepository.findByUser_EmailContainingIgnoreCaseAndOrderDateBetweenOrderByOrderDateDesc(
                    customerEmail, dateFrom.atStartOfDay(), dateTo.plusDays(1).atStartOfDay());
        }
        if (hasEmail) {
            return orderRepository.findByUser_EmailContainingIgnoreCaseOrderByOrderDateDesc(customerEmail);
        }
        if (hasDates) {
            return orderRepository.findByOrderDateBetweenOrderByOrderDateDesc(
                    dateFrom.atStartOfDay(), dateTo.plusDays(1).atStartOfDay());
        }
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        OrderStatus previous = order.getStatus();
        if (status == OrderStatus.CANCELLED && previous != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (product != null) {
                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                    productRepository.save(product);
                }
            }
        }

        order.setStatus(status);
        orderRepository.save(order);
    }
}
