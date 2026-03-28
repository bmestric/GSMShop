package hr.bmestric.gsmshop.service;

import hr.bmestric.gsmshop.dto.Cart;
import jakarta.servlet.http.HttpSession;

public interface CartService {

    Cart getCart(HttpSession session);

    void addToCart(HttpSession session, Long productId, int quantity);

    void updateQuantity(HttpSession session, Long productId, int quantity);

    void removeFromCart(HttpSession session, Long productId);

    void clearCart(HttpSession session);
}
