package hr.bmestric.gsmshop.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer stockQuantity;
    private Long categoryId;
    private String categoryName;
    private String productType;

    // Phone-specific fields
    private String screenResolution;
    private Double screenSize;
    private Integer batteryCapacity;
    private Integer chargingPower;
    private Integer ramGb;
    private Integer romGb;
    private String processor;
    private List<CameraResponse> cameras;

    // Accessory-specific fields
    private String accessoryType;
    private String compatibleModels;
}
