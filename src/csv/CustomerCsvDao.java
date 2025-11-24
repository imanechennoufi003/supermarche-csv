package csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import model.Customer;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class CustomerCsvDao {
    private final String path;

    public CustomerCsvDao(String path) {
        this.path = path;
    }

    public List<Customer> findAll() throws Exception {
        List<Customer> customers = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] row;
            boolean header = true;
            while ((row = reader.readNext()) != null) {
                if (header) { header = false; continue; }
                // Assurez-vous que Customer a le bon constructeur (4 args)
                customers.add(new Customer(row[0], row[1], row[2], row[3])); 
            }
        }
        return customers;
    }

    public void saveAll(List<Customer> customers) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            writer.writeNext(new String[]{"id","name","email","phone"});
            for (Customer c : customers) {
                // Assurez-vous que Customer a les Getters
                writer.writeNext(new String[]{c.getId(), c.getName(), c.getEmail(), c.getPhone()});
            }
        }
    }
}