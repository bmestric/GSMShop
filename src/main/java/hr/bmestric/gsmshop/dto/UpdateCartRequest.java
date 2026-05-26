package hr.bmestric.gsmshop.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartRequest {

    @Min(1)
    private int quantity;
}
