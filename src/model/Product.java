package model;

// Importation nécessaire pour 'toList()' si vous utilisez Java 16+

public class Product {
    private String id;
    private String name;
    private String category;
    private double price;
    private int stock;
    private int minStock;

    // CONSTRUCTEUR UTILISÉ PAR ProductCsvDao.findAll()
    public Product(String id, String name, String category, double price, int stock, int minStock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.minStock = minStock;
    }

    // Getters et Setters (nécessaires pour les DAOs et les Services)
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public int getMinStock() { return minStock; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setMinStock(int minStock) { this.minStock = minStock; }
    
    // Ajout d'un constructeur vide pour une meilleure compatibilité des librairies
    public Product() {}

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - Prix: %.2f, Stock: %d (Min: %d)", id, name, category, price, stock, minStock);
    }
}