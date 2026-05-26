package hr.bmestric.gsmshop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {

    @NotBlank
    private String paymentMethod;

    @NotNull
    @Valid
    private DeliveryInfo delivery;
}
