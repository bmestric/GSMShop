package hr.bmestric.gsmshop.listener;

import hr.bmestric.gsmshop.entity.Accessory;
import hr.bmestric.gsmshop.entity.Camera;
import hr.bmestric.gsmshop.entity.Category;
import hr.bmestric.gsmshop.entity.Phone;
import hr.bmestric.gsmshop.enums.CameraType;
import hr.bmestric.gsmshop.enums.ScreenResolution;
import hr.bmestric.gsmshop.repository.CategoryRepository;
import hr.bmestric.gsmshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSeeder {

    private static final String APERTURE_F22 = "f/2.2";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }
        Category smartphones = categoryRepository.findByName("Smartphones").orElse(null);
        Category cases = categoryRepository.findByName("Cases & Covers").orElse(null);
        Category chargers = categoryRepository.findByName("Chargers & Cables").orElse(null);

        if (smartphones != null) {
            seedPhones(smartphones);
        }
        if (cases != null) {
            seedAccessories(cases, chargers);
        }
        log.info("Sample products seeded.");
    }

    private void seedPhones(Category category) {
        createPhone(category, "Samsung Galaxy S24 Ultra",
                "Flagship Samsung phone with S Pen",
                new BigDecimal("1299.99"), 15,
                ScreenResolution.LTPO_QHD_PLUS, 6.8, 5000, 45, 12, 256, "Snapdragon 8 Gen 3",
                cam(CameraType.MAIN, 200, "f/1.7"),
                cam(CameraType.ULTRAWIDE, 12, APERTURE_F22),
                cam(CameraType.TELEPHOTO, 50, "f/2.4"),
                cam(CameraType.TELEPHOTO, 10, "f/4.9"));

        createPhone(category, "iPhone 15 Pro Max",
                "Apple flagship with titanium design",
                new BigDecimal("1199.99"), 20,
                ScreenResolution.SUPER_RETINA_XDR, 6.7, 4441, 27, 8, 256, "A17 Pro",
                cam(CameraType.MAIN, 48, "f/1.78"),
                cam(CameraType.ULTRAWIDE, 12, APERTURE_F22),
                cam(CameraType.TELEPHOTO, 12, "f/2.8"));

        createPhone(category, "Xiaomi 14",
                "Powerful mid-range flagship",
                new BigDecimal("699.99"), 30,
                ScreenResolution.AMOLED_PLUS, 6.36, 4610, 90, 12, 256, "Snapdragon 8 Gen 3",
                cam(CameraType.MAIN, 50, "f/1.6"),
                cam(CameraType.ULTRAWIDE, 50, APERTURE_F22),
                cam(CameraType.TELEPHOTO, 50, "f/2.0"));

        createPhone(category, "Google Pixel 8 Pro",
                "Best camera phone with AI features",
                new BigDecimal("999.99"), 10,
                ScreenResolution.LTPO_OLED, 6.7, 5050, 30, 12, 128, "Tensor G3",
                cam(CameraType.MAIN, 50, "f/1.68"),
                cam(CameraType.ULTRAWIDE, 48, "f/1.95"),
                cam(CameraType.TELEPHOTO, 48, "f/2.8"));
    }

    private void seedAccessories(Category cases, Category chargers) {
        createAccessory(cases, "Samsung Clear Case S24", "Official Samsung clear case",
                new BigDecimal("29.99"), 50, "Case", "Samsung Galaxy S24, S24+, S24 Ultra");
        createAccessory(cases, "iPhone 15 Silicone Case", "Apple silicone case with MagSafe",
                new BigDecimal("49.99"), 40, "Case", "iPhone 15, 15 Pro, 15 Pro Max");
        if (chargers != null) {
            createAccessory(chargers, "Anker 65W USB-C Charger", "Fast GaN charger",
                    new BigDecimal("34.99"), 60, "Charger", "Universal USB-C");
            createAccessory(chargers, "Apple MagSafe Charger", "Wireless MagSafe charger",
                    new BigDecimal("39.99"), 35, "Charger", "iPhone 12 and later");
        }
    }

    private Camera cam(CameraType type, int megapixels, String aperture) {
        Camera c = new Camera();
        c.setType(type);
        c.setMegapixels(megapixels);
        c.setAperture(aperture);
        return c;
    }

    private void createPhone(Category category, String name, String desc, BigDecimal price,
                             int stock, ScreenResolution resolution, double screenSize,
                             int battery, int charging, int ram, int rom,
                             String processor, Camera... cameras) {
        Phone phone = new Phone();
        phone.setName(name);
        phone.setDescription(desc);
        phone.setPrice(price);
        phone.setImageUrl("/images/products/" + name.toLowerCase().replace(" ", "-") + ".jpg");
        phone.setStockQuantity(stock);
        phone.setCategory(category);
        phone.setScreenResolution(resolution);
        phone.setScreenSize(screenSize);
        phone.setBatteryCapacity(battery);
        phone.setChargingPower(charging);
        phone.setRamGb(ram);
        phone.setRomGb(rom);
        phone.setProcessor(processor);
        for (Camera cam : cameras) {
            cam.setPhone(phone);
        }
        phone.setCameras(List.of(cameras));
        productRepository.save(phone);
    }

    private void createAccessory(Category category, String name, String desc, BigDecimal price,
                                 int stock, String type, String compatible) {
        Accessory accessory = new Accessory();
        accessory.setName(name);
        accessory.setDescription(desc);
        accessory.setPrice(price);
        accessory.setImageUrl("/images/products/" + name.toLowerCase().replace(" ", "-") + ".jpg");
        accessory.setStockQuantity(stock);
        accessory.setCategory(category);
        accessory.setAccessoryType(type);
        accessory.setCompatibleModels(compatible);
        productRepository.save(accessory);
    }
}
