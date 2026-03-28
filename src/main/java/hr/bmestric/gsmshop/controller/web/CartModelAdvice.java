package hr.bmestric.gsmshop.controller.web;

import hr.bmestric.gsmshop.dto.Cart;
import hr.bmestric.gsmshop.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class CartModelAdvice {

    private final CartService cartService;

    @ModelAttribute("cartItemCount")
    public int cartItemCount(HttpSession session) {
        Cart cart = cartService.getCart(session);
        return cart.getTotalItemCount();
    }
}
