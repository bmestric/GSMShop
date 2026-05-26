package hr.bmestric.gsmshop.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CartItemResponse {

    private Long productId;
    private String name;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;
    private BigDecimal subtotal;
}
