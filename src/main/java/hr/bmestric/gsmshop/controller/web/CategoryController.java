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
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "category/list";
    }

    @GetMapping("/{id}")
    public String categoryProducts(@PathVariable Long id, Model model) {
        categoryService.findById(id).ifPresent(cat -> {
            model.addAttribute("category", cat);
            model.addAttribute("products", productService.findByCategoryId(id));
        });
        return "category/products";
    }
}
