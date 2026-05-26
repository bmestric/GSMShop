package hr.bmestric.gsmshop.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CameraResponse {

    private String type;
    private Integer megapixels;
    private String aperture;
}
