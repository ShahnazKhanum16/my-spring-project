import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CSVProcessingService {
    private static final String DB_URL = "jdbc:h2:~/test";
    private static final String DB_USER = "SA";
    private static final String DB_PASSWORD = "";

    public void processCSVFiles(List<String> filePaths) {
        ExecutorService executor = Executors.newCachedThreadPool();

        for (String filePath : filePaths) {
            executor.execute(() -> processCSVFile(filePath));
        }

        executor.shutdown();
    }

    private void processCSVFile(String filePath) {
        List<String[]> data = loadDataFromFile(filePath);
        persistDataToDatabase(data);
        verifyInsertedData(); // Verify inserted data after each CSV file processing
    }

    private List<String[]> loadDataFromFile(String filePath) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                rows.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(rows);
        return rows;
    }

    private void persistDataToDatabase(List<String[]> data) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO people (id, name) VALUES (?, ?) ON CONFLICT DO NOTHING"; // For PostgreSQL syntax
            // For H2, use: String sql = "MERGE INTO people (id, name) KEY(id) VALUES (?, ?)"; 

            PreparedStatement statement = connection.prepareStatement(sql);

            for (String[] row : data) {
                if (row.length >= 2) { // Assuming each row has at least 2 columns (id and name)
                    long id = Long.parseLong(row[0]);
                    String name = row[1];

                    // Check if record already exists based on ID (Assuming ID is unique)
                    if (!isRecordExists(connection, id)) {
                        statement.setLong(1, id); // Assuming ID is in the first column
                        statement.setString(2, name); // Assuming name is in the second column
                        statement.addBatch();
                    }
                }
            }

            int[] result = statement.executeBatch();

            // Check if the batch execution was successful
            if (result.length > 0) {
                System.out.println("Data insertion successful for file.");
            } else {
                System.out.println("No new records inserted for file.");
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            System.err.println("Data insertion failed: " + e.getMessage());
        }
    }

    // Method to check if a record exists based on ID
    private boolean isRecordExists(Connection connection, long id) throws SQLException {
        String sql = "SELECT id FROM people WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next(); // Returns true if record exists
    }

    private void verifyInsertedData() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM people";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Inserted data verification:");

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");

                System.out.println("ID: " + id + ", Name: " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while verifying inserted data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        CSVProcessingService csvService = new CSVProcessingService();
        List<String> csvFiles = new ArrayList<>();
        //csvFiles.add("./src/main/students.csv");
        csvFiles.add("./src/main/staff.csv"); // Add your CSV file paths here

        csvService.processCSVFiles(csvFiles);
    }
}
