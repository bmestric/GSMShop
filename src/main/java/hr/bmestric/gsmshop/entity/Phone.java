package hr.bmestric.gsmshop.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("PHONE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phone extends Product {

    private String screenResolution;

    private Double screenSize;

    private String cameraSpec;

    private Integer batteryCapacity;

    private Integer chargingPower;

    private Integer ramGb;

    private Integer romGb;

    private String processor;
}
