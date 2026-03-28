package hr.bmestric.gsmshop.controller.web;

import hr.bmestric.gsmshop.entity.Accessory;
import hr.bmestric.gsmshop.entity.Camera;
import hr.bmestric.gsmshop.entity.Phone;
import hr.bmestric.gsmshop.entity.Product;
import hr.bmestric.gsmshop.service.CategoryService;
import hr.bmestric.gsmshop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.findAll());
        return "admin/product/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/product/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        productService.findById(id).ifPresent(p -> model.addAttribute("product", p));
        model.addAttribute("categories", categoryService.findAll());
        return "admin/product/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam String productType,
                       @RequestParam(required = false) Long id,
                       @RequestParam String name,
                       @RequestParam(required = false) String description,
                       @RequestParam BigDecimal price,
                       @RequestParam(required = false) String imageUrl,
                       @RequestParam Integer stockQuantity,
                       @RequestParam Long categoryId,
                       HttpServletRequest request,
                       RedirectAttributes redirectAttributes) {
        Product product = "PHONE".equals(productType)
                ? buildPhone(id, request)
                : buildAccessory(id, request);

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setStockQuantity(stockQuantity);
        categoryService.findById(categoryId).ifPresent(product::setCategory);

        productService.save(product);
        redirectAttributes.addFlashAttribute("success", "Product saved successfully.");
        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Product deleted.");
        return "redirect:/admin/products";
    }

    private Phone buildPhone(Long id, HttpServletRequest req) {
        Phone phone = id != null
                ? (Phone) productService.findById(id).orElse(new Phone())
                : new Phone();
        phone.setScreenResolution(req.getParameter("screenResolution"));
        phone.setScreenSize(parseDouble(req.getParameter("screenSize")));
        phone.setBatteryCapacity(parseInteger(req.getParameter("batteryCapacity")));
        phone.setChargingPower(parseInteger(req.getParameter("chargingPower")));
        phone.setRamGb(parseInteger(req.getParameter("ramGb")));
        phone.setRomGb(parseInteger(req.getParameter("romGb")));
        phone.setProcessor(req.getParameter("processor"));

        String[] camTypes = req.getParameterValues("camType");
        String[] camMp = req.getParameterValues("camMegapixels");
        String[] camAperture = req.getParameterValues("camAperture");
        List<Camera> cameras = new ArrayList<>();
        if (camTypes != null) {
            for (int i = 0; i < camTypes.length; i++) {
                if (!camTypes[i].isBlank()) {
                    Camera cam = new Camera();
                    cam.setType(camTypes[i]);
                    cam.setMegapixels(parseInteger(camMp[i]));
                    cam.setAperture(camAperture != null ? camAperture[i] : null);
                    cam.setPhone(phone);
                    cameras.add(cam);
                }
            }
        }
        phone.getCameras().clear();
        phone.getCameras().addAll(cameras);
        return phone;
    }

    private Accessory buildAccessory(Long id, HttpServletRequest req) {
        Accessory acc = id != null
                ? (Accessory) productService.findById(id).orElse(new Accessory())
                : new Accessory();
        acc.setAccessoryType(req.getParameter("accessoryType"));
        acc.setCompatibleModels(req.getParameter("compatibleModels"));
        return acc;
    }

    private Integer parseInteger(String val) {
        return val != null && !val.isBlank() ? Integer.parseInt(val) : null;
    }

    private Double parseDouble(String val) {
        return val != null && !val.isBlank() ? Double.parseDouble(val) : null;
    }
}
