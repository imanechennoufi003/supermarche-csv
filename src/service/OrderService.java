package service;

import model.Order;
import model.OrderItem;
import model.Product;

public class OrderService {
    private final InventoryService inventory;

    public OrderService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public double computeTotal(Order order) {
        return order.getItems().stream()
            .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
            .sum();
    }

    public void applyOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            Product p = inventory.findById(item.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Produit " + item.getProductId() + " introuvable"));
            if (p.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Stock insuffisant pour " + p.getName());
            }
            // Décrémente le stock en mémoire (dans le service)
            p.setStock(p.getStock() - item.getQuantity());
        }
        order.setTotal(computeTotal(order));
        // La sauvegarde finale sera gérée par Main.java
    }
}