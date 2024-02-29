import service.FileMethods;
import service.serviceimpl.FileMethodsImpl;
import model.Product;
import service.ProductService;
import service.serviceimpl.ProductServiceImpl;
import util.Message;
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
    static String[] animation = {"\033[31m⠋⠙⠹⠸⠼⠴⠦⠧⠇⠏⠋⠙⠹⠸⠼⠴⠦⠧⠇⠏\033[0m"};
    final static int progressBarWidth = 30;
    public static void loadDataUntilReady(AtomicBoolean isDataReady) {
        new Thread(() -> {
            try {
                int i = 0;
                while (!isDataReady.get()) {
                    String progressBar = generateProgressBar(i % animation[0].length());
                    out.print("\r\033[36mLoading data " + animation[0].charAt(i % animation[0].length()) +
                            " \033[32m" + progressBar + " \033[0m");
                    Thread.sleep(getRandomSpeed());
                    i++;
                }
            } catch (Exception e) {
                Message.errMessage(e.getMessage());
            }
        }).start();
    }
    static String generateProgressBar(int animationIndex) {
        StringBuilder progressBar = new StringBuilder("[");
        int filledWidth = (animationIndex * MainApplication.progressBarWidth) / animation[0].length();
        for (int i = 0; i < MainApplication.progressBarWidth; i++) {
            progressBar.append(i == filledWidth ? "\033[33m█" : " ");
        }
        progressBar.append("\033[0m]");
        return progressBar.toString();
    }
    static int getRandomSpeed() {
        return (int) (Math.random() * 150 + 50);
    }
    public static void main(String[] args) throws InterruptedException {
        AtomicBoolean isReady = new AtomicBoolean(false);

        Thread waitForLoading = new Thread(() -> {
            try {
                List<Product> dataSourceProducts = fileMethods.readProductsFromFile(DATA_SOURCE_FILE);
                List<Product> transferProducts = fileMethods.readProductsFromFile(TRANSFER_FILE);
                productList.addAll(dataSourceProducts);
                productList.addAll(transferProducts);
                fileMethods.checkFileForCommit(productList);
                Thread.sleep(9);
                Duration readFile = timeOperation(() -> {
                    loadDataUntilReady(isReady);
                });

                isReady.set(true);
                System.out.println("\n loading Completed!: " + readFile.toSeconds() + "s");
            } catch (Exception e) {
                out.println(e.getMessage());
            }

        });
        waitForLoading.start();
        waitForLoading.join();

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
                    case "s" -> productService.searchProductByName(productList);
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
                    case "y" -> fileMethods.destroy(DATA_SOURCE_FILE);
                    default -> Message.errMessage("Wrong option");
                }
            }catch (Exception e){
                Message.errMessage("Error message: " + e.getMessage());
            }
        } while (true);

    }
}