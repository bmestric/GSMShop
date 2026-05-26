package hr.bmestric.gsmshop.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {

    private Long id;
    private LocalDateTime orderDate;
    private String status;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryPostalCode;
    private String deliveryCountry;
    private String deliveryPhone;
    private List<OrderItemResponse> items;
}
