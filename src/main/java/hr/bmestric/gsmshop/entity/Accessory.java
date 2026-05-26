package hr.bmestric.gsmshop.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ACCESSORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Accessory extends Product {

    private String accessoryType;

    private String compatibleModels;
}
