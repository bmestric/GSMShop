package hr.bmestric.gsmshop.repository;

import hr.bmestric.gsmshop.entity.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhoneRepository extends JpaRepository<Phone, Long> {

    @Query("SELECT DISTINCT p FROM Phone p LEFT JOIN FETCH p.cameras WHERE p.id IN :ids AND p.deleted = false")
    List<Phone> findWithCamerasByIdIn(@Param("ids") List<Long> ids);
}
