package app;
import csv.CustomerCsvDao;
import csv.OrderCsvDao;
import csv.ProductCsvDao;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.Customer;
import model.Order;
import model.OrderItem;
import model.Product;
import service.InventoryService;
import service.OrderService;

public class Main {
    public static void main(String[] args) { 
        
        ProductCsvDao productDao = new ProductCsvDao("data/products.csv");
        CustomerCsvDao customerDao = new CustomerCsvDao("data/customers.csv");
        OrderCsvDao orderDao = new OrderCsvDao("data/orders.csv", "data/order_items.csv");

        List<Product> products = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();
        List<Order> orders = new ArrayList<>();

        try {
            products = productDao.findAll();
            customers = customerDao.findAll();
            orders = orderDao.findAll();
            System.out.printf("✅ Données chargées : %d produits, %d clients, %d commandes.\n", 
                products.size(), customers.size(), orders.size());
        } catch (Exception e) {
            System.err.println("❌ Erreur au chargement des données. Assurez-vous que les fichiers CSV existent dans le dossier 'data/' et que le JAR OpenCSV est bien inclus.\n" + e.getMessage());
            return;
        }

        // INITIALISATION DES SERVICES
        InventoryService inventory = new InventoryService(products);
        OrderService orderService = new OrderService(inventory);


        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Supermarché ===");
            System.out.println("1. Lister produits");
            System.out.println("2. Ajouter produit");
            System.out.println("3. Modifier stock");
            System.out.println("4. Alertes stock");
            System.out.println("5. Lister clients");
            System.out.println("6. Ajouter client");
            System.out.println("7. Créer commande");
            System.out.println("8. Sauvegarder et Quitter");
            System.out.print("Choix: ");
            String choice = sc.nextLine();

            try {
                switch (choice) {
                    case "1" -> inventory.all().forEach(System.out::println);
                    
                    case "2" -> {
                        System.out.print("ID produit: "); String id = sc.nextLine();
                        System.out.print("Nom: "); String name = sc.nextLine();
                        System.out.print("Catégorie: "); String category = sc.nextLine();
                        System.out.print("Prix: "); double price = Double.parseDouble(sc.nextLine());
                        System.out.print("Stock initial: "); int stock = Integer.parseInt(sc.nextLine());
                        System.out.print("Stock minimum: "); int minStock = Integer.parseInt(sc.nextLine());
                        
                        inventory.addProduct(new Product(id, name, category, price, stock, minStock));
                        System.out.println("Produit ajouté.");
                    }
                    
                    case "3" -> {
                        System.out.print("ID produit à modifier: "); String id = sc.nextLine();
                        System.out.print("Quantité à ajouter (ex: 10) ou retirer (ex: -5): "); int delta = Integer.parseInt(sc.nextLine());
                        inventory.updateStock(id, delta);
                        System.out.println("Stock mis à jour.");
                    }
                    
                    case "4" -> {
                        List<Product> alerts = inventory.lowStockAlerts();
                        if (alerts.isEmpty()) {
                            System.out.println("✅ Aucun produit en stock bas.");
                        } else {
                            alerts.forEach(p -> System.out.printf("🚨 ALERTE: %s (Stock: %d, Min: %d)\n", p.getName(), p.getStock(), p.getMinStock()));
                        }
                    }
                    
                    case "5" -> customers.forEach(System.out::println);
                    
                    case "6" -> {
                        System.out.print("ID client: "); String id = sc.nextLine();
                        System.out.print("Nom: "); String name = sc.nextLine();
                        System.out.print("Email: "); String email = sc.nextLine();
                        System.out.print("Téléphone: "); String phone = sc.nextLine();
                        
                        if (customers.stream().anyMatch(c -> c.getId().equals(id))) {
                            throw new IllegalArgumentException("Client ID déjà existant.");
                        }
                        
                        customers.add(new Customer(id, name, email, phone));
                        System.out.println("Client ajouté.");
                    }
                    
                    case "7" -> {
                        String orderId = "O" + String.format("%03d", orders.size() + 1);
                        String currentDate = LocalDate.now().toString();
                        
                        System.out.println("\n--- Nouvelle Commande " + orderId + " (" + currentDate + ") ---");
                        System.out.print("ID client (ex: C001): "); 
                        String customerId = sc.nextLine();
                        
                        if (customers.stream().noneMatch(c -> c.getId().equals(customerId))) {
                             System.out.println("❌ Erreur: Client " + customerId + " introuvable.");
                             break;
                        }

                        Order order = new Order(orderId, customerId, currentDate);
                        boolean addingItems = true;
                        
                        while (addingItems) {
                            System.out.print("ID produit (ou 'done' pour terminer): "); 
                            String pid = sc.nextLine();
                            
                            if (pid.equalsIgnoreCase("done")) {
                                addingItems = false;
                                continue;
                            }
                            
                            System.out.print("Quantité: "); 
                            int qty = Integer.parseInt(sc.nextLine());
                            
                            Product p = inventory.findById(pid)
                                .orElseThrow(() -> new IllegalArgumentException("Produit " + pid + " introuvable."));
                            
                            if (qty <= 0) throw new IllegalArgumentException("La quantité doit être positive.");
                            
                            order.addItem(new OrderItem(orderId, pid, qty, p.getPrice()));
                            System.out.println("-> Article " + p.getName() + " ajouté. (" + qty + " unités)");
                        }
                        
                        if (order.getItems().isEmpty()) {
                            System.out.println("Commande annulée car aucun article n'a été ajouté.");
                        } else {
                            orderService.applyOrder(order);
                            orders.add(order);
                            System.out.println("✅ Commande créée et stock mis à jour: " + order);
                        }
                    }
                            case "8" -> {
                        productDao.saveAll(inventory.all()); 
                        customerDao.saveAll(customers);
                        orderDao.saveAll(orders);
                        running = false;
                        System.out.println("Toutes les données ont été sauvegardées. Bye!");
                    }
                    
                    default -> System.out.println("Choix invalide.");
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur: " + e.getMessage());
            }
        }
    }
}