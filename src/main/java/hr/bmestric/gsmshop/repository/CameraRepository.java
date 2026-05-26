package hr.bmestric.gsmshop.repository;

import hr.bmestric.gsmshop.entity.Camera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CameraRepository extends JpaRepository<Camera, Long> {

    List<Camera> findByPhoneId(Long phoneId);
}
