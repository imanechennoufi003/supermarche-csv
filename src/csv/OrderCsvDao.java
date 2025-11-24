package csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import model.Order;
import model.OrderItem;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class OrderCsvDao {
    private final String ordersPath;
    private final String itemsPath;

    public OrderCsvDao(String ordersPath, String itemsPath) {
        this.ordersPath = ordersPath;
        this.itemsPath = itemsPath;
    }

    public List<Order> findAll() throws Exception {
        List<Order> orders = new ArrayList<>();

        // Charger les commandes
        try (CSVReader reader = new CSVReader(new FileReader(ordersPath))) {
            String[] row;
            boolean header = true;
            while ((row = reader.readNext()) != null) {
                if (header) { header = false; continue; }
                Order o = new Order(row[0], row[1], row[2]);
                o.setTotal(Double.parseDouble(row[3]));
                orders.add(o);
            }
        }

        // Charger les items
        try (CSVReader reader = new CSVReader(new FileReader(itemsPath))) {
            String[] row;
            boolean header = true;
            while ((row = reader.readNext()) != null) {
                if (header) { header = false; continue; }
                String orderId = row[0];
                Order order = orders.stream()
                        .filter(o -> o.getId().equals(orderId))
                        .findFirst().orElse(null);
                if (order != null) {
                    // Assurez-vous que OrderItem a le bon constructeur (4 args)
                    order.addItem(new OrderItem(orderId, row[1],
                            Integer.parseInt(row[2]),
                            Double.parseDouble(row[3])));
                }
            }
        }

        return orders;
    }

    public void saveAll(List<Order> orders) throws Exception {
        // Sauvegarde des commandes
        try (CSVWriter writer = new CSVWriter(new FileWriter(ordersPath))) {
            writer.writeNext(new String[]{"id","customerId","date","total"});
            for (Order o : orders) {
                writer.writeNext(new String[]{o.getId(), o.getCustomerId(), o.getDate(), String.valueOf(o.getTotal())});
            }
        }

        // Sauvegarde des items de commandes
        try (CSVWriter writer = new CSVWriter(new FileWriter(itemsPath))) {
            writer.writeNext(new String[]{"orderId","productId","quantity","unitPrice"});
            for (Order o : orders) {
                for (OrderItem item : o.getItems()) {
                    writer.writeNext(new String[]{
                            item.getOrderId(),
                            item.getProductId(),
                            String.valueOf(item.getQuantity()),
                            String.valueOf(item.getUnitPrice())
                    });
                }
            }
        }
    }
}