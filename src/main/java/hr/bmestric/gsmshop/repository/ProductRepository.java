package hr.bmestric.gsmshop.repository;

import hr.bmestric.gsmshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByDeletedFalse();

    List<Product> findByDeletedFalseAndCategoryId(Long categoryId);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByIdInAndDeletedFalse(List<Long> ids);
}
