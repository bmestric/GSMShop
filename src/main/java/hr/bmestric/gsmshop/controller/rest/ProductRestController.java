package hr.bmestric.gsmshop.controller.rest;

import hr.bmestric.gsmshop.dto.ApiError;
import hr.bmestric.gsmshop.dto.CameraResponse;
import hr.bmestric.gsmshop.dto.ProductResponse;
import hr.bmestric.gsmshop.entity.Accessory;
import hr.bmestric.gsmshop.entity.Phone;
import hr.bmestric.gsmshop.entity.Product;
import hr.bmestric.gsmshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> list(
            @RequestParam(required = false) Long categoryId) {
        List<Product> products = categoryId != null
                ? productService.findByCategoryId(categoryId)
                : productService.findAll();
        return ResponseEntity.ok(products.stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiError.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Product not found: " + id)
                            .timestamp(LocalDateTime.now())
                            .build());
        }
        return ResponseEntity.ok(toResponse(product.get()));
    }

    ProductResponse toResponse(Product product) {
        ProductResponse.ProductResponseBuilder builder = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .productType(product.getProductType());

        if (product instanceof Phone phone) {
            builder.screenResolution(phone.getScreenResolution() != null
                    ? phone.getScreenResolution().name() : null)
                    .screenSize(phone.getScreenSize())
                    .batteryCapacity(phone.getBatteryCapacity())
                    .chargingPower(phone.getChargingPower())
                    .ramGb(phone.getRamGb())
                    .romGb(phone.getRomGb())
                    .processor(phone.getProcessor())
                    .cameras(phone.getCameras().stream()
                            .map(c -> CameraResponse.builder()
                                    .type(c.getType().name())
                                    .megapixels(c.getMegapixels())
                                    .aperture(c.getAperture())
                                    .build())
                            .toList());
        } else if (product instanceof Accessory accessory) {
            builder.accessoryType(accessory.getAccessoryType())
                    .compatibleModels(accessory.getCompatibleModels());
        }

        return builder.build();
    }
}
