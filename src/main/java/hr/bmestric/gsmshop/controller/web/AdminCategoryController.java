package hr.bmestric.gsmshop.controller.web;

import hr.bmestric.gsmshop.entity.Category;
import hr.bmestric.gsmshop.service.CategoryService;
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
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/category/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        categoryService.findById(id).ifPresent(cat -> model.addAttribute("category", cat));
        return "admin/category/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String name,
                       @RequestParam(required = false) String description,
                       @RequestParam(required = false) String imageUrl,
                       RedirectAttributes redirectAttributes) {
        Category category = id != null
                ? categoryService.findById(id).orElse(new Category())
                : new Category();
        category.setName(name);
        category.setDescription(description);
        category.setImageUrl(imageUrl);
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("success", "Category saved successfully.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Category deleted.");
        return "redirect:/admin/categories";
    }
}
