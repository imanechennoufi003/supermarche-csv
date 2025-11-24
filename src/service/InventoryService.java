package service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import model.Product; // AJOUTÉ

public class InventoryService {
    private final List<Product> products;

    public InventoryService(List<Product> products) {
        this.products = products;
    }

    public List<Product> all() { return products; }

    public Optional<Product> findById(String id) {
        return products.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public void addProduct(Product p) {
        if (findById(p.getId()).isPresent()) {
            throw new IllegalArgumentException("Produit déjà existant.");
        }
        products.add(p);
    }

    public void updateStock(String id, int delta) {
        Product p = findById(id).orElseThrow(() -> new IllegalArgumentException("Produit introuvable"));
        p.setStock(p.getStock() + delta);
    }

    public List<Product> lowStockAlerts() {
        // Utiliser toList() (Java 16+) ou collect(Collectors.toList()) (Java 8/11/15)
        return products.stream().filter(p -> p.getStock() <= p.getMinStock()).collect(Collectors.toList());
    }
}