package hr.bmestric.gsmshop.enums;

public enum ScreenResolution {
    HD_PLUS("HD+ (720 × 1600)", 720, 1600),
    FHD_PLUS("FHD+ (1080 × 2400)", 1080, 2400),
    QHD_PLUS("QHD+ (1440 × 3200)", 1440, 3200),
    AMOLED_PLUS("AMOLED+ (1200 × 2670)", 1200, 2670),
    SUPER_RETINA_XDR("Super Retina XDR (1290 × 2796)", 1290, 2796),
    LTPO_OLED("LTPO OLED (1344 × 2992)", 1344, 2992),
    LTPO_QHD_PLUS("LTPO QHD+ (1440 × 3120)", 1440, 3120);

    private final String label;
    private final int width;
    private final int height;

    ScreenResolution(String label, int width, int height) {
        this.label = label;
        this.width = width;
        this.height = height;
    }

    public String getLabel() {
        return label;
    }

    public int getWidth() {
        return width;
    }


    public long getPixelCount() {
        return (long) width * height;
    }
}
