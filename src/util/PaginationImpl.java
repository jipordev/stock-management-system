package util;

import java.io.*;
import java.util.Scanner;

public class PaginationImpl implements Pagination{
    private static final String CONFIG_FILE = "config.bak";
    @Override
    public int setNewRow() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return 10;
    }

    @Override
    public void setPageSize(Scanner scanner) {
        System.out.println("#".repeat(20));
        System.out.println("Set Row to Display in Table");
        int newRowSize;
        do {
            System.out.print("Enter Row (greater than 0): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next();
            }
            newRowSize = scanner.nextInt();
            scanner.nextLine();
        } while (newRowSize <= 0);
        System.out.print("Do you want to set new row size (Y/N): ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            savePageSize(newRowSize);
        }
    }

    @Override
    public int savePageSize(int pageSize) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            writer.write(String.valueOf(pageSize));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return pageSize;
    }
}