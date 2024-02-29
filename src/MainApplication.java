import service.FileMethods;
import service.serviceimpl.FileMethodsImpl;
import model.Product;
import service.ProductService;
import service.serviceimpl.ProductServiceImpl;
import util.Pagination;
import util.PaginationImpl;
import util.exception.StringRegex;
import view.MenuImpl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public static Duration timeOperation(Runnable operation) {
        long startTime = System.nanoTime();
        operation.run();
        long endTime = System.nanoTime();
        return Duration.ofNanos(endTime - startTime);
    }
    public static void loadDataUntilReady(AtomicBoolean isDataReady) {
        final String[] animation = {"|", "/", "-", "\\"};
        Thread startLoading = new Thread(() -> {
            try {
                int i = 0;
                while (!isDataReady.get()) {
                    System.out.print("\rLoading data " + animation[i % animation.length]);
                    Thread.sleep(100);
                    i++;
                }
            } catch (InterruptedException e) {
                out.println(e.getMessage());
            }
        });
        startLoading.start();
    }

    public static void main(String[] args) {
        AtomicBoolean isReady = new AtomicBoolean(false);

        Duration readFile = timeOperation(()->{

        List<Product> dataSourceProducts = fileMethods.readProductsFromFile(DATA_SOURCE_FILE);
        List<Product> transferProducts = fileMethods.readProductsFromFile(TRANSFER_FILE);
        productList.addAll(dataSourceProducts);
        productList.addAll(transferProducts);
        fileMethods.checkFileForCommit(productList);
            loadDataUntilReady(isReady);
        });
        System.out.println("\n Completed! "+readFile.toSeconds()+"s");
        isReady.set(true);

        do {
            int pageNumber = 1;
            int pageSize = pagination.setNewRow();
            menu.displayBanner();
            menu.displayMainMenu();
            try {
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
                    case "c" -> {
                        fileMethods.displayCommit(productList);
                        fileMethods.checkFileForCommit(productList);
                    }
                    case "k" -> {
                        String backupFilePath = fileMethods.backupFileDir();
                        System.out.print("Are you sure to Backup [Y/N]: ");
                        String ch = scanner.nextLine();
                        if (StringRegex.validateString(ch,"[yYnN]")){
                            if (ch.equalsIgnoreCase("y")) {
                                fileMethods.backUpData(DATA_SOURCE_FILE, backupFilePath);
                            }
                        } else {
                            out.println("Invalid input. please enter 'Y' or 'N'.");
                        }
                    }
                    case "t" -> fileMethods.restoreData();
                    case "h" -> menu.displayHelp();
                    case "x" -> {
                        fileMethods.checkFileForCommit(productList);
                        System.exit(0);
                    }
                }
            }catch (Exception e){
                out.println("Error message: " + e.getMessage());
            }
        } while (true);
    }
}