package hr.bmestric.gsmshop.service.impl;

import hr.bmestric.gsmshop.dto.Cart;
import hr.bmestric.gsmshop.dto.CartItem;
import hr.bmestric.gsmshop.entity.AppUser;
import hr.bmestric.gsmshop.entity.Order;
import hr.bmestric.gsmshop.entity.OrderItem;
import hr.bmestric.gsmshop.entity.Product;
import hr.bmestric.gsmshop.enums.PaymentMethod;
import hr.bmestric.gsmshop.repository.AppUserRepository;
import hr.bmestric.gsmshop.repository.OrderRepository;
import hr.bmestric.gsmshop.repository.ProductRepository;
import hr.bmestric.gsmshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AppUserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Order createOrder(String userEmail, Cart cart, PaymentMethod paymentMethod,
                             String paypalTransactionId) {
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentMethod(paymentMethod);
        order.setPaypalTransactionId(paypalTransactionId);
        order.setTotalAmount(cart.getTotal());

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

        return orderRepository.save(order);
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
}
