package hr.bmestric.gsmshop.listener;

import hr.bmestric.gsmshop.entity.AppUser;
import hr.bmestric.gsmshop.entity.Category;
import hr.bmestric.gsmshop.enums.Role;
import hr.bmestric.gsmshop.repository.AppUserRepository;
import hr.bmestric.gsmshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppStartupListener {

    private final AppUserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductSeeder productSeeder;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        seedAdminUser();
        seedCategories();
        productSeeder.seedProducts();
        log.info("Application startup data seeding completed.");
    }

    private void seedAdminUser() {
        if (userRepository.existsByEmail("admin@gsmshop.hr")) {
            return;
        }
        AppUser admin = new AppUser();
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setEmail("admin@gsmshop.hr");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        log.info("Default admin user created: admin@gsmshop.hr");
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) {
            return;
        }
        createCategory("Smartphones", "Latest smartphones from top brands",
                "/images/categories/smartphones.jpg");
        createCategory("Cases & Covers", "Protective cases and covers for your phone",
                "/images/categories/cases.jpg");
        createCategory("Chargers & Cables", "Charging accessories and cables",
                "/images/categories/chargers.jpg");
        createCategory("Headphones", "Wired and wireless headphones",
                "/images/categories/headphones.jpg");
        createCategory("Screen Protectors", "Tempered glass and film screen protectors",
                "/images/categories/screen-protectors.jpg");
        log.info("Sample categories seeded.");
    }

    private void createCategory(String name, String description, String imageUrl) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setImageUrl(imageUrl);
        categoryRepository.save(category);
    }
}
