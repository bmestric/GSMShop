package hr.bmestric.gsmshop.service.impl;

import hr.bmestric.gsmshop.dto.Cart;
import hr.bmestric.gsmshop.dto.CartItem;
import hr.bmestric.gsmshop.entity.Product;
import hr.bmestric.gsmshop.repository.ProductRepository;
import hr.bmestric.gsmshop.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final String CART_SESSION_KEY = "cart";
    private final ProductRepository productRepository;

    @Override
    public Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    @Override
    public void addToCart(HttpSession session, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        Cart cart = getCart(session);
        int currentQty = cart.getItemQuantity(productId);
        int requestedTotal = currentQty + quantity;

        if (product.getStockQuantity() <= 0) {
            throw new IllegalStateException("Product is out of stock: " + product.getName());
        }
        if (requestedTotal > product.getStockQuantity()) {
            throw new IllegalStateException(
                    "Not enough stock for " + product.getName()
                            + ". Available: " + product.getStockQuantity()
                            + ", requested: " + requestedTotal);
        }

        CartItem item = new CartItem(
                product.getId(),
                product.getName(),
                product.getPrice(),
                quantity,
                product.getImageUrl()
        );
        cart.addItem(item);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    public void updateQuantity(HttpSession session, Long productId, int quantity) {
        Cart cart = getCart(session);
        if (quantity <= 0) {
            cart.removeItem(productId);
        } else {
            cart.updateQuantity(productId, quantity);
        }
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    public void removeFromCart(HttpSession session, Long productId) {
        Cart cart = getCart(session);
        cart.removeItem(productId);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    public void clearCart(HttpSession session) {
        Cart cart = getCart(session);
        cart.clear();
        session.setAttribute(CART_SESSION_KEY, cart);
    }
}
