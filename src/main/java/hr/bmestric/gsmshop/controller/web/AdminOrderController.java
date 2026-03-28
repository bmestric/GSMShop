package hr.bmestric.gsmshop.controller.web;

import hr.bmestric.gsmshop.entity.Order;
import hr.bmestric.gsmshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;

    @GetMapping
    public String list(@RequestParam(required = false) Long customerId,
                       @RequestParam(required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                       @RequestParam(required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                       Model model) {
        List<Order> orders = findOrders(customerId, dateFrom, dateTo);
        model.addAttribute("orders", orders);
        model.addAttribute("customerId", customerId);
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        return "admin/order/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        orderRepository.findById(id).ifPresent(order -> model.addAttribute("order", order));
        return "admin/order/detail";
    }

    private List<Order> findOrders(Long customerId, LocalDate dateFrom, LocalDate dateTo) {
        boolean hasCustomer = customerId != null;
        boolean hasDates = dateFrom != null && dateTo != null;

        if (hasCustomer && hasDates) {
            return orderRepository.findByUserIdAndOrderDateBetween(
                    customerId, dateFrom.atStartOfDay(), dateTo.plusDays(1).atStartOfDay());
        }
        if (hasCustomer) {
            return orderRepository.findByUserId(customerId);
        }
        if (hasDates) {
            return orderRepository.findByOrderDateBetween(
                    dateFrom.atStartOfDay(), dateTo.plusDays(1).atStartOfDay());
        }
        return orderRepository.findAllByOrderByOrderDateDesc();
    }
}
