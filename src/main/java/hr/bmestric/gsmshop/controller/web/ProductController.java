package hr.bmestric.gsmshop.controller.web;

import hr.bmestric.gsmshop.service.CategoryService;
import hr.bmestric.gsmshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "product/list";
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        productService.findById(id).ifPresent(product ->
                model.addAttribute("product", product));
        return "product/detail";
    }
}
