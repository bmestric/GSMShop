package hr.bmestric.gsmshop.service.impl;

import hr.bmestric.gsmshop.entity.Product;
import hr.bmestric.gsmshop.enums.OrderStatus;
import hr.bmestric.gsmshop.repository.OrderItemRepository;
import hr.bmestric.gsmshop.repository.ProductRepository;
import hr.bmestric.gsmshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findByDeletedFalse();
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return productRepository.findByDeletedFalseAndCategoryId(categoryId);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findByIds(List<Long> ids) {
        return productRepository.findByIdInAndDeletedFalse(ids);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));

        if (orderItemRepository.existsByProductIdAndOrder_StatusNot(id, OrderStatus.COMPLETED)) {
            throw new IllegalStateException(
                    "Cannot delete \"" + product.getName() + "\" — it exists in one or more active orders. " +
                    "All orders containing this product must be completed first.");
        }

        product.setDeleted(true);
        productRepository.save(product);
    }
}
