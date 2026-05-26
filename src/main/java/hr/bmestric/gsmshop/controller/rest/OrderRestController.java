package hr.bmestric.gsmshop.controller.rest;

import hr.bmestric.gsmshop.dto.ApiError;
import hr.bmestric.gsmshop.dto.Cart;
import hr.bmestric.gsmshop.dto.OrderItemResponse;
import hr.bmestric.gsmshop.dto.OrderRequest;
import hr.bmestric.gsmshop.dto.OrderResponse;
import hr.bmestric.gsmshop.entity.Order;
import hr.bmestric.gsmshop.enums.PaymentMethod;
import hr.bmestric.gsmshop.service.CartService;
import hr.bmestric.gsmshop.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderService orderService;
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> listOrders(Authentication auth) {
        return ResponseEntity.ok(
                orderService.findByUserEmail(auth.getName())
                        .stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrder(@PathVariable Long id, Authentication auth) {
        return orderService.findById(id)
                .filter(o -> o.getUser().getEmail().equals(auth.getName()))
                .map(o -> (Object) toResponse(o))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(error(HttpStatus.NOT_FOUND.value(), "Order not found: " + id)));
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(@Valid @RequestBody OrderRequest request,
                                              Authentication auth,
                                              HttpSession session) {
        Cart cart = cartService.getCart(session);
        if (cart.getItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error(HttpStatus.BAD_REQUEST.value(), "Cart is empty"));
        }

        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error(HttpStatus.BAD_REQUEST.value(),
                            "Invalid payment method: " + request.getPaymentMethod()));
        }

        try {
            Order order = orderService.createOrder(
                    auth.getName(), cart, paymentMethod, null, request.getDelivery());
            cartService.clearCart(session);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(order));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .paymentMethod(order.getPaymentMethod().name())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryCity(order.getDeliveryCity())
                .deliveryPostalCode(order.getDeliveryPostalCode())
                .deliveryCountry(order.getDeliveryCountry())
                .deliveryPhone(order.getDeliveryPhone())
                .items(order.getItems().stream()
                        .map(item -> OrderItemResponse.builder()
                                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                                .quantity(item.getQuantity())
                                .priceAtPurchase(item.getPriceAtPurchase())
                                .subtotal(item.getPriceAtPurchase()
                                        .multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                                .build())
                        .toList())
                .build();
    }

    private ApiError error(int status, String message) {
        return ApiError.builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
