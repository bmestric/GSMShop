package hr.bmestric.gsmshop.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("PHONE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phone extends Product {

    private String screenResolution;

    private Double screenSize;

    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Camera> cameras = new ArrayList<>();

    private Integer batteryCapacity;

    private Integer chargingPower;

    private Integer ramGb;

    private Integer romGb;

    private String processor;
}
