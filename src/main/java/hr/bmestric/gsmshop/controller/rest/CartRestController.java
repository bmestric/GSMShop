package hr.bmestric.gsmshop.controller.rest;

import hr.bmestric.gsmshop.dto.AddToCartRequest;
import hr.bmestric.gsmshop.dto.ApiError;
import hr.bmestric.gsmshop.dto.Cart;
import hr.bmestric.gsmshop.dto.CartItemResponse;
import hr.bmestric.gsmshop.dto.CartResponse;
import hr.bmestric.gsmshop.dto.UpdateCartRequest;
import hr.bmestric.gsmshop.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartRestController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(HttpSession session) {
        return ResponseEntity.ok(toResponse(cartService.getCart(session)));
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addToCart(@Valid @RequestBody AddToCartRequest request,
                                            HttpSession session) {
        try {
            cartService.addToCart(session, request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(toResponse(cartService.getCart(session)));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long productId,
                                             @Valid @RequestBody UpdateCartRequest request,
                                             HttpSession session) {
        try {
            cartService.updateQuantity(session, productId, request.getQuantity());
            return ResponseEntity.ok(toResponse(cartService.getCart(session)));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long productId,
                                                   HttpSession session) {
        cartService.removeFromCart(session, productId);
        return ResponseEntity.ok(toResponse(cartService.getCart(session)));
    }

    @DeleteMapping
    public ResponseEntity<CartResponse> clearCart(HttpSession session) {
        cartService.clearCart(session);
        return ResponseEntity.ok(toResponse(cartService.getCart(session)));
    }

    private CartResponse toResponse(Cart cart) {
        return CartResponse.builder()
                .items(cart.getItems().stream()
                        .map(item -> CartItemResponse.builder()
                                .productId(item.getProductId())
                                .name(item.getProductName())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .imageUrl(item.getImageUrl())
                                .subtotal(item.getSubtotal())
                                .build())
                        .toList())
                .totalItems(cart.getTotalItemCount())
                .totalPrice(cart.getTotal())
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
