package hr.bmestric.gsmshop.service;

import hr.bmestric.gsmshop.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> findAll();

    List<Product> findByCategoryId(Long categoryId);

    Optional<Product> findById(Long id);

    Product save(Product product);

    void deleteById(Long id);
}
