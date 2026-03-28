package hr.bmestric.gsmshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Camera extends BaseEntity {

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer megapixels;

    private String aperture;

    @ManyToOne
    @JoinColumn(name = "phone_id", nullable = false)
    private Phone phone;
}
