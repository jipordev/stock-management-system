package service.serviceimpl;

import model.Product;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import service.FileMethods;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FileMethodsImpl implements FileMethods {
    static Table table = new Table(1, BorderStyle.UNICODE_BOX_DOUBLE_BORDER, ShownBorders.SURROUND);
    static Scanner scanner = new Scanner(System.in);

    @Override
    public List<Product> readProductsFromFile(String fileName) {
        List<Product> productList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    Product product = new Product();
                    product.setProductCode(parts[0].trim());
                    product.setProductName(parts[1].trim());
                    product.setProductPrice(Double.parseDouble(parts[2].trim()));
                    product.setQty(Integer.parseInt(parts[3].trim()));
                    product.setDate(LocalDate.parse(parts[4].trim()));
                    product.setStatus(parts[5].trim());
                    productList.add(product);
                } else {
                    System.out.println("Invalid data in file: " + line);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return productList;
    }

    @Override
    public void writeToFile(List<Product> productList, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true))) {
            for (Product p : productList) {
                writer.write(p.getProductCode() + "," +
                        p.getProductName() + "," +
                        p.getProductPrice() + "," +
                        p.getQty() + "," +
                        p.getDate().toString() + "," +
                        p.getStatus());
                writer.flush();
                writer.newLine();
            }
            System.out.println("Data written to file successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    @Override
    public String backupFileDir() {
        String backupDirectory = "backup/";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        String  backupFileName = "backupfile_" + timestamp + ".bak";
        return backupDirectory + backupFileName;
    }

    @Override
    public void writeTransferRecord(Product product, String transferFileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(transferFileName, true))) {
            // Format the product details and write them to the transfer file
            writer.write(product.getProductCode() + "," +
                    product.getProductName() + "," +
                    product.getProductPrice() + "," +
                    product.getQty() + "," +
                    product.getDate() + "," +
                    product.getStatus());
            writer.flush();
            writer.newLine();
            System.out.println("Transfer record written successfully.");
        } catch (IOException e) {
            System.err.println("Error writing transfer record: " + e.getMessage());
        }
    }

    @Override
    public synchronized boolean hasData(String fileName) {
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return reader.readLine() != null;
        } catch (IOException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void backUpData(String sourceFilePath, String backupFilePath) {
        try {
            Path sourcePath = Path.of(sourceFilePath);
            Path backupPath = Path.of(backupFilePath);
            if (Files.exists(sourcePath)) {
                // Create the backup directory if it doesn't exist
                Files.createDirectories(backupPath.getParent());

                // Use buffered streams for better IO performance
                try (InputStream inStream = Files.newInputStream(sourcePath);
                     OutputStream outStream = Files.newOutputStream(backupPath)) {
                    byte[] buffer = new byte[8192]; // Adjust buffer size as needed
                    int bytesRead;
                    while ((bytesRead = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }

                System.out.println("Backup created successfully.");
            } else {
                System.out.println("Source file does not exist.");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void restoreData() {
        listingBackupFiles();
        String backupDirectory = "backup/";
        String restoreDirectory = "restore/";

        System.out.print("Choose the number of the file you want to restore: ");
        int fileNumber = Integer.parseInt(new Scanner(System.in).nextLine());

        // Validate user input
        File backupDir = new File(backupDirectory);
        File[] files = backupDir.listFiles();
        if (files != null && fileNumber >= 1 && fileNumber <= files.length) {
            File selectedBackupFile = files[fileNumber - 1];

            // Create restore directory if it doesn't exist
            File restoreDir = new File(restoreDirectory);
            if (!restoreDir.exists()) {
                restoreDir.mkdirs();
            }

            // Construct the path for the restored file
            String restoredFilePath = restoreDirectory + selectedBackupFile.getName();

            // Copy the selected backup file to the restore directory
            try {
                Files.copy(selectedBackupFile.toPath(), Paths.get(restoredFilePath), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File successfully restored to: " + restoredFilePath);
            } catch (IOException e) {
                System.err.println("Error restoring file: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid file number selected.");
        }
    }

    @Override
    public void listingBackupFiles() {
        String backUpDirectory = "backup/";
        File backupDir = new File(backUpDirectory);
        if (backupDir.exists() && backupDir.isDirectory()){
            File[] files = backupDir.listFiles();
            if (files != null && files.length >0) {
                Table table1 = new Table(1, BorderStyle.UNICODE_BOX_DOUBLE_BORDER, ShownBorders.SURROUND); // Initialize table here
                System.out.println("Back up files: ");
                for (int i = 0; i<files.length; i++){
                    table1.addCell("   "+(i+1)+". "+files[i].getName()+"   ");
                }
                System.out.println(table1.render());
            } else {
                System.out.println("There are no backup files found");
            }
        } else {
            System.out.println("Backup directory does not exist or is not a directory");
        }
    }

    @Override
    public void commit(List<Product> productList ,String dataSourceFile, String transferFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(transferFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(dataSourceFile, true));
             FileWriter truncateWriter = new FileWriter(transferFile, false)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String status = parts[parts.length - 1].trim(); // assuming status is the last field

                switch (status) {
                    case "new" -> {
                        String updatedLine = line.substring(0, line.lastIndexOf(',')) + ",null";
                        // Write the updated line to the transfer file (temporarily)
                        writer.write(updatedLine);
                        writer.newLine();
                    }
                    case "delete" -> {
                        String productCodeToDelete = parts[0].trim();
                        productList.removeIf(product -> product.getProductCode().equals(productCodeToDelete));
                    }
                    case "update" -> {

                    }
                }
            }
            // Commit is done, now truncate the transfer file
            truncateWriter.write(""); // This will clear the content of the file
        } catch (IOException e) {
            // Handle IO exceptions
            System.out.println(e.getMessage());
        }
    }
}