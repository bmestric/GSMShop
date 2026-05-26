package hr.bmestric.gsmshop.entity;

import hr.bmestric.gsmshop.enums.CameraType;
import hr.bmestric.gsmshop.enums.ScreenResolution;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
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

    @Enumerated(EnumType.STRING)
    private ScreenResolution screenResolution;

    private Double screenSize;

    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Camera> cameras = new ArrayList<>();

    private Integer batteryCapacity;

    private Integer chargingPower;

    private Integer ramGb;

    private Integer romGb;

    private String processor;

    @Transient
    public Integer getMainCameraMp() {
        return cameras.stream()
                .filter(c -> c.getType() == CameraType.MAIN)
                .map(Camera::getMegapixels)
                .findFirst()
                .orElse(0);
    }
}
