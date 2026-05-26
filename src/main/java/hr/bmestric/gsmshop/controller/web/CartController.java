package hr.bmestric.gsmshop.controller.web;

import hr.bmestric.gsmshop.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        model.addAttribute("cart", cartService.getCart(session));
        return "cart/view";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                           @RequestParam(defaultValue = "1") int quantity,
                           HttpSession session,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        try {
            cartService.addToCart(session, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Product added to cart!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/products");
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId,
                                 @RequestParam int quantity,
                                 HttpSession session) {
        cartService.updateQuantity(session, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        cartService.removeFromCart(session, id);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        cartService.clearCart(session);
        return "redirect:/cart";
    }
}
