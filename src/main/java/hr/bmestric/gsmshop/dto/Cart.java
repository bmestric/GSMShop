package hr.bmestric.gsmshop.dto;

import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class Cart implements Serializable {

    private final List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem item) {
        Optional<CartItem> existing = findItem(item.getProductId());
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + item.getQuantity());
        } else {
            items.add(item);
        }
    }

    public void updateQuantity(Long productId, int quantity) {
        findItem(productId).ifPresent(item -> item.setQuantity(quantity));
    }

    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
    }

    public void clear() {
        items.clear();
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItemCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public int getItemQuantity(Long productId) {
        return findItem(productId).map(CartItem::getQuantity).orElse(0);
    }

    private Optional<CartItem> findItem(Long productId) {
        return items.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst();
    }
}
