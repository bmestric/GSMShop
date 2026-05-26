package hr.bmestric.gsmshop.controller.web;

import hr.bmestric.gsmshop.repository.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/login-history")
@RequiredArgsConstructor
public class AdminLoginHistoryController {

    private final LoginHistoryRepository loginHistoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("history",
                loginHistoryRepository.findAll(Sort.by(Sort.Direction.DESC, "loginTime")));
        return "admin/login-history";
    }
}
