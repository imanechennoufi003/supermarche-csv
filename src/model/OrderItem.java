package model;

public class OrderItem {
    private String orderId;
    private String productId;
    private int quantity;
    private double unitPrice;

    // CONSTRUCTEUR UTILISÉ PAR OrderCsvDao.findAll() et Main.java
    public OrderItem(String orderId, String productId, int quantity, double unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    // Ajout d'un constructeur vide
    public OrderItem() {}

    public String getOrderId() { return orderId; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    
    // Setters
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setProductId(String productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    @Override
    public String toString() {
        return String.format("%s x %d (%.2f / unité)", productId, quantity, unitPrice);
    }
}