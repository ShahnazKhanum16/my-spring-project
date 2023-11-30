import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvData {
    private String filePath;
    private List<String[]> data;

    public CsvData(String filePath) {
        this.filePath = filePath;
        this.data = loadData();
    }

    private List<String[]> loadData() {
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
        return rows;
    }

    public List<String[]> getRows() {
        return data;
    }

    public String[] getColumn(int columnNumber) {
        List<String> column = new ArrayList<>();
        for (String[] row : data) {
            if (columnNumber < row.length) {
                column.add(row[columnNumber]);
            }
        }
        return column.toArray(new String[0]);
    }

    public String getValue(int rowNumber, int columnNumber) {
        String[] row = data.get(rowNumber);
        if (columnNumber < row.length) {
            return row[columnNumber];
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        String filePath = "example.csv"; // Replace this with your CSV file path
        CsvData csvData = new CsvData(filePath);

        // Get all rows
        List<String[]> allRows = csvData.getRows();
        System.out.println("All Rows:");
        for (String[] row : allRows) {
            for (String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }

        // Get a specific column (e.g., column 2)
        String[] column2 = csvData.getColumn(1); // Remember, columns are zero-indexed
        System.out.println("\nColumn 2:");
        for (String cell : column2) {
            System.out.println(cell);
        }

        // Get a specific value (e.g., row 1, column 3)
        String value = csvData.getValue(0, 2); // Remember, rows and columns are zero-indexed
        System.out.println("\nValue at row 1, column 3:");
        System.out.println(value);
    }
}