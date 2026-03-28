package hr.bmestric.gsmshop.repository;

import hr.bmestric.gsmshop.entity.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
}
