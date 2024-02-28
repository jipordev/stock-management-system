import filemethods.FileMethods;
import filemethods.FileMethodsImpl;
import model.Product;
import service.ProductService;
import service.ProductServiceImpl;
import util.Pagination;
import util.PaginationImpl;
import view.MenuImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.*;

public class MainApplication {
    private static final String DATA_SOURCE_FILE = "product.bak";
    private static final String TRANSFER_FILE = "transproduct.bak";
    static Scanner scanner = new Scanner(in);
    static MenuImpl menu = new MenuImpl();
    static Pagination pagination = new PaginationImpl();
    static ProductService productService = new ProductServiceImpl();
    static FileMethods fileMethods = new FileMethodsImpl();
    static List<Product> productList = new ArrayList<>();

    public static void main(String[] args) {
        List<Product> transferProducts = fileMethods.readProductsFromFile(TRANSFER_FILE);
        List<Product> dataSourceProducts = fileMethods.readProductsFromFile(DATA_SOURCE_FILE);
        productList.addAll(dataSourceProducts);
        productList.addAll(transferProducts);
        fileMethods.checkFileForCommit(productList);
        do {
            int pageNumber = 1;
            int pageSize = pagination.setNewRow();
            menu.displayBanner();
            menu.displayMainMenu();
            out.print("Choose an option: ");
            String op = scanner.nextLine();
            switch (op) {
                case "l" -> productService.displayAllProduct(productList, pageNumber, pageSize);
                case "m" -> productService.randomRecord(productList);
                case "w" -> productService.createProduct(productList);
                case "r" -> productService.readProduct(productList);
                case "e" -> productService.updateProduct(productList);
                case "d" -> productService.deleteProduct(productList);
                case "s" -> productService.searchProductByName();
                case "o" -> pagination.setPageSize(scanner);
                case "c" -> fileMethods.displayCommit(transferProducts);
                case "k" -> {
                    String backupFilePath = fileMethods.backupFileDir();
                    System.out.print("Are you sure to Backup [Y/N]: ");
                    String ch = scanner.nextLine();
                    if (ch.equalsIgnoreCase("y")) {
                        fileMethods.backUpData(DATA_SOURCE_FILE, backupFilePath);
                    }
                }
                case "t" -> fileMethods.restoreData();
                case "h" -> menu.displayHelp();
                case "x" -> {
                    transferProducts = fileMethods.readProductsFromFile(TRANSFER_FILE);
                    fileMethods.checkFileForCommit(productList);
                    System.exit(0);
                }
            }
        } while (true);
    }
}