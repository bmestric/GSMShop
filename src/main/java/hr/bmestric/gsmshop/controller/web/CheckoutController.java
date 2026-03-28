package hr.bmestric.gsmshop.controller.web;

import hr.bmestric.gsmshop.dto.Cart;
import hr.bmestric.gsmshop.entity.Order;
import hr.bmestric.gsmshop.enums.PaymentMethod;
import hr.bmestric.gsmshop.service.CartService;
import hr.bmestric.gsmshop.service.OrderService;
import hr.bmestric.gsmshop.service.PayPalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final PayPalService payPalService;

    @GetMapping
    public String checkoutForm(HttpSession session, Model model) {
        Cart cart = cartService.getCart(session);
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cart", cart);
        return "checkout/form";
    }

    @PostMapping("/cod")
    public String checkoutCod(HttpSession session, Principal principal, Model model) {
        Cart cart = cartService.getCart(session);
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        Order order = orderService.createOrder(
                principal.getName(), cart, PaymentMethod.CASH_ON_DELIVERY, null);
        cartService.clearCart(session);
        model.addAttribute("order", order);
        return "checkout/success";
    }

    @PostMapping("/paypal/create")
    public String createPayPalOrder(HttpSession session, HttpServletRequest request) {
        Cart cart = cartService.getCart(session);
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort();
        String approvalUrl = payPalService.createOrder(
                cart.getTotal(),
                baseUrl + "/checkout/paypal/success",
                baseUrl + "/checkout/paypal/cancel");
        return "redirect:" + approvalUrl;
    }

    @GetMapping("/paypal/success")
    public String paypalSuccess(@RequestParam("token") String token,
                                HttpSession session, Principal principal, Model model) {
        String transactionId = payPalService.captureOrder(token);
        Cart cart = cartService.getCart(session);
        Order order = orderService.createOrder(
                principal.getName(), cart, PaymentMethod.PAYPAL, transactionId);
        cartService.clearCart(session);
        model.addAttribute("order", order);
        return "checkout/success";
    }

    @GetMapping("/paypal/cancel")
    public String paypalCancel() {
        return "checkout/cancel";
    }
}
