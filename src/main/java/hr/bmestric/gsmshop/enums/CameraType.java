package hr.bmestric.gsmshop.enums;

public enum CameraType {
    MAIN("Main"),
    ULTRAWIDE("Ultrawide"),
    TELEPHOTO("Telephoto"),
    MACRO("Macro"),
    SELFIE("Selfie"),
    DEPTH("Depth"),
    PERISCOPE("Periscope"),
    TOF("ToF / LiDAR");

    private final String displayName;

    CameraType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
