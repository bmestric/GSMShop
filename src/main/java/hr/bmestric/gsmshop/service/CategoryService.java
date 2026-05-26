package hr.bmestric.gsmshop.service;

import hr.bmestric.gsmshop.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> findAll();

    Optional<Category> findById(Long id);

    Category save(Category category);

    void deleteById(Long id);
}
