package service.serviceimpl;

import service.FileMethods;
import model.Product;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import service.ProductService;
import util.Message;
import util.Pagination;
import util.PaginationImpl;
import view.Menu;
import view.MenuImpl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProductServiceImpl implements ProductService {
    private static final String FILE_DIRECTORY = "files/";
    private static final String DATA_SOURCE_FILE = "product.bak";
    private static final String TRANSFER_FILE = "transproduct.bak";

    private final Path DATA_SOURCE_FILE_PATH = Paths.get(FILE_DIRECTORY, DATA_SOURCE_FILE);
    private final Path TRANSFER_FILE_PATH = Paths.get(FILE_DIRECTORY, TRANSFER_FILE);

    private final FileMethods fileMethods = new FileMethodsImpl();
    private final Pagination pagination = new PaginationImpl();
    private final Menu menu = new MenuImpl();
    private final Scanner scanner = new Scanner(System.in);


    public static Duration timeOperation(Runnable operation) {
        long startTime = System.nanoTime();
        operation.run();
        long endTime = System.nanoTime();
        return Duration.ofNanos(endTime - startTime);
    }

    public void loadDataUntilReady(AtomicBoolean isDataReady) {
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
                Message.errMessage(e.getMessage());
            }
        });
        startLoading.start();
    }

    @Override
    public void randomRecord(List<Product> productList) {
        System.out.print(">Enter amount of record : ");
        int randomNumber = Integer.parseInt(scanner.nextLine());
        Product[] products = new Product[randomNumber];
        System.out.println("Are you sure want to random " + randomNumber + " Products?[Y/n]: ");
        String ch = new Scanner(System.in).nextLine();
        //loading is starting
        if (ch.equalsIgnoreCase("y")) {
            AtomicBoolean isDataReady = new AtomicBoolean(false);
            loadDataUntilReady(isDataReady);
            try {
                Thread.sleep(100);
                // random time is start
                Duration randomTime = timeOperation(() -> {
                    for (int i = 0; i < randomNumber; i++) {
                        products[i] = new Product();
                        products[i].setProductCode("CSTAD" + i);
                        products[i].setProductName("Product::" + i);
                        products[i].setProductPrice(0.0);
                        products[i].setQty(0);
                        products[i].setDate(LocalDate.now());
                        products[i].setStatus("null");
                    }
                    productList.addAll(List.of(products));
                    fileMethods.writeToFile(productList, DATA_SOURCE_FILE);
                    System.out.println("Random is completed!!");
                    // loading is ready(random time is end )
                    isDataReady.set(true);
                });
                System.out.println("Write " + randomNumber + " Products Speed : " + randomTime.toSeconds() + " s");
            } catch (InterruptedException e) {
                Message.errMessage(e.getMessage());
            }
        }
    }

    @Override
    public void createProduct(List<Product> productList) {
        try {
            Product product = new Product();
            System.out.print("Enter CODE : ");
            product.setProductCode(scanner.nextLine());
            System.out.print("Enter NAME : ");
            product.setProductName(scanner.nextLine());
            System.out.print("Enter PRICE : ");
            product.setProductPrice(Double.parseDouble(scanner.nextLine()));
            System.out.print("Enter QTY : ");
            product.setQty(Integer.parseInt(scanner.nextLine()));
            product.setDate(LocalDate.now());
            product.setStatus("new");
            productList.add(product);
            menu.confirmation(productList,product.getProductCode());
            System.out.print("Are you sure to create? (Y/N): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("y")){
                fileMethods.writeTransferRecord(product,TRANSFER_FILE);

                System.out.println("New product created successfully.");
            } else {
                productList.remove(product);
                System.out.println("Fail to create product");
            }
        } catch (Exception e) {
            Message.errMessage(e.getMessage());
        }
    }

    @Override
    public void deleteProduct(List<Product> productList) {
        Table table = new Table(1, BorderStyle.UNICODE_BOX_DOUBLE_BORDER,ShownBorders.SURROUND);
        // Read products from the data source file
        System.out.print("Enter code to delete: ");
        String codeToDelete = scanner.nextLine();

        // Find the product to delete
        Optional<Product> productToDeleteOpt = productList.stream()
                .filter(product -> product.getProductCode().equals(codeToDelete))
                .findFirst();

        // Check if the product exists1
        if (productToDeleteOpt.isPresent()) {
            Product productToDelete = productToDeleteOpt.get();
            // Add the product to the transfer file with status "delete"
            Product transferProduct = new Product(
                    productToDelete.getProductCode(),
                    productToDelete.getProductName(),
                    productToDelete.getProductPrice(),
                    productToDelete.getQty(),
                    productToDelete.getDate(),
                    "delete"
            );
            fileMethods.writeTransferRecord(transferProduct, TRANSFER_FILE);

            table.addCell("Product code: "+productToDelete.getProductCode());
            table.addCell("Product name: "+productToDelete.getProductName());
            table.addCell("Product price: "+productToDelete.getProductPrice());
            table.addCell("Product quantity: "+productToDelete.getQty());
            table.addCell("Product date: "+productToDelete.getDate());
            table.addCell("Product status: "+productToDelete.getStatus());
            System.out.println(table.render());
            // Remove the product from the original file
            System.out.print("Are you sure to delete (Y/N): ");
            if (scanner.nextLine().equalsIgnoreCase("y")){
                productList.remove(productToDelete);
                System.out.println("#################");
                System.out.println("Product deleted successfully.");
            } else {
                System.out.println("#################");
                System.out.println("Deleting product canceled...");
            }
        } else {
            System.out.println("Product not found.");
        }
    }

    @Override
    public void readProduct(List<Product> productList) {
        try {
            Table table = new Table(1, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE, ShownBorders.SURROUND);
            System.out.print("Enter product code : ");
            String code = scanner.nextLine();
            System.out.println("#######################################");

            Product foundProduct = null;
            // Iterate over the productList in reverse order to find the last product with the given code
            for (int i = productList.size() - 1; i >= 0; i--) {
                Product product = productList.get(i);
                if (product.getProductCode().equals(code)) {
                    foundProduct = product;
                    break; // Exit the loop once the last product with the given code is found
                }
            }

            if (foundProduct != null) {
                table.addCell("Product Code: " + foundProduct.getProductCode());
                table.addCell("Product Name: " + foundProduct.getProductName());
                table.addCell("Product Price: " + foundProduct.getProductPrice().toString());
                table.addCell("Product Qty: " + foundProduct.getQty().toString());
                table.addCell("Product Date: " + foundProduct.getDate().toString());
                table.addCell("Product Status: " + foundProduct.getStatus());
            } else {
                System.out.println("No product found with the given code.");
            }
            System.out.println(table.render());
        } catch (Exception e) {
            Message.errMessage(e.getMessage());
        }
    }

    @Override
    public void updateProduct(List<Product> productList) {
       try {
           System.out.println("""
        1. Update all
        2. Update name
        3. Update Price
        4. Update Qty
        """);
           System.out.print("Choose option to update : ");
           int op = Integer.parseInt(scanner.nextLine());
           switch (op) {
               case 1 -> {
                   System.out.print("Enter product code : ");
                   String code = scanner.nextLine();

                   // Check if the product exists
                   boolean found = false;
                   for (Product product : productList) {
                       if (product.getProductCode().equals(code)) {
                           menu.confirmation(productList, code); // Display current details
                           System.out.println("-----Enter new product details-----");
                           System.out.print("Enter new product name: ");
                           String newName = scanner.nextLine();
                           System.out.print("Enter new product price: ");
                           double newPrice = Double.parseDouble(scanner.nextLine());
                           System.out.print("Enter new product quantity: ");
                           int newQty = Integer.parseInt(scanner.nextLine());

                           System.out.println("Please confirm updating all details (Y/N): ");
                           String confirmation = scanner.nextLine().trim().toLowerCase();
                           if (confirmation.equals("y")) {
                               // Update product details
                               product.setProductName(newName);
                               product.setProductPrice(newPrice);
                               product.setQty(newQty);
                               product.setDate(LocalDate.now());
                               product.setStatus("update");

                               // Write updated product to transfer file
                               fileMethods.writeTransferRecord(product, TRANSFER_FILE);
                               System.out.println("Product updated successfully.");
                           } else {
                               System.out.println("Update canceled.");
                           }
                           found = true;
                           break; // Exiting the loop once the product is found and updated
                       }
                   }
                   if (!found) {
                       System.out.println("Product not found");
                   }
               }
               case 2 -> {
                   System.out.print("Enter product code: ");
                   String code = scanner.nextLine();
                   for (Product product : productList) {
                       if (product.getProductCode().equals(code)) {
                           menu.confirmation(productList, code); // Display current details
                           System.out.print("Enter new product name: ");
                           String newName = scanner.nextLine();
                           System.out.println("Please confirm updating product name (Y/N): ");
                           String confirmation = scanner.nextLine().trim().toLowerCase();
                           if (confirmation.equals("y")) {
                               product.setProductName(newName);
                               product.setDate(LocalDate.now());
                               product.setStatus("update");
                               fileMethods.writeTransferRecord(product, TRANSFER_FILE);
                               System.out.println("Product name updated successfully.");
                           } else {
                               System.out.println("Update canceled.");
                           }
                           return;
                       }
                   }
                   System.out.println("Product not found");
               }
               case 3 -> {
                   System.out.print("Enter product code: ");
                   String code = scanner.nextLine();
                   for (Product product : productList) {
                       if (product.getProductCode().equals(code)) {
                           menu.confirmation(productList, code); // Display current details
                           System.out.print("Enter new product price: ");
                           double newPrice = Double.parseDouble(scanner.nextLine());
                           System.out.println("Please confirm updating product name (Y/N): ");
                           String confirmation = scanner.nextLine().trim().toLowerCase();
                           if (confirmation.equals("y")) {
                               product.setProductPrice(newPrice);
                               product.setDate(LocalDate.now());
                               product.setStatus("update");
                               fileMethods.writeTransferRecord(product, TRANSFER_FILE);
                               System.out.println("Product name updated successfully.");
                           } else {
                               System.out.println("Update canceled.");
                           }
                           return;
                       }
                   }
                   System.out.println("Product not found");
               }
               case 4 -> {
                   System.out.print("Enter product code: ");
                   String code = scanner.nextLine();
                   for (Product product : productList) {
                       if (product.getProductCode().equals(code)) {
                           menu.confirmation(productList, code); // Display current details
                           System.out.print("Enter new product qty: ");
                           int newQty = Integer.parseInt(scanner.nextLine());
                           System.out.println("Please confirm updating product name (Y/N): ");
                           String confirmation = scanner.nextLine().trim().toLowerCase();
                           if (confirmation.equals("y")) {
                               product.setQty(newQty);
                               product.setDate(LocalDate.now());
                               product.setStatus("update");
                               fileMethods.writeTransferRecord(product, TRANSFER_FILE);
                               System.out.println("Product name updated successfully.");
                           } else {
                               System.out.println("Update canceled.");
                           }
                           return;
                       }
                   }
                   System.out.println("Product not found");
               }
               default -> System.out.println("Invalid update option");
           }
       } catch (Exception e) {
           Message.errMessage(e.getMessage());
       }
    }


    @Override
    public void displayAllProduct(List<Product> productList, int pageNumber, int pageSize) {
        boolean isTrue;
        do {
            int startIndex = (pageNumber - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, productList.size());
            Table table = new Table(6, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE, ShownBorders.ALL);
            CellStyle cellStyle = new CellStyle(CellStyle.HorizontalAlign.center);
            System.out.println("#######################################");
            table.addCell("     Product Code     ");
            table.addCell("     Product Name     ");
            table.addCell("     Product Price     ");
            table.addCell("     Product QTY     ");
            table.addCell("     Product Date     ");
            table.addCell("  Status  ");
            for (int i = startIndex; i < endIndex; i++) {
                try {
                    Product product = productList.get(i);
                    table.addCell(product.getProductCode(), cellStyle);
                    table.addCell(product.getProductName(), cellStyle);
                    table.addCell(product.getProductPrice().toString(), cellStyle);
                    table.addCell(product.getQty().toString(), cellStyle);
                    table.addCell(product.getDate().toString(), cellStyle);
                    table.addCell(product.getStatus(), cellStyle);
                } catch (IndexOutOfBoundsException e) {
                    Message.errMessage("Error: Index out of bounds.");
                } catch (NullPointerException e) {
                    Message.errMessage("Error: Null pointer encountered.");
                } catch (Exception e) {
                    System.out.println("Error: An unexpected error occurred.");
                    Message.errMessage(e.getMessage());
                }
            }
            System.out.println(table.render());
            System.out.println("o" + "~".repeat(125) + "o");
            int totalPage = (int) Math.ceil((double) productList.size() / pageSize); // Calculate total pages
            System.out.printf("Page: %d of %d \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   Total Records: %d%n", pageNumber, totalPage, productList.size());
            System.out.print("Page Navigation\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t(F)irst  (P)revious  (G)oto  (N)ext  (L)ast \n");
            System.out.println("o" + "~".repeat(125) + "o");
            String option;
            isTrue = true;
            System.out.print(">(B)ack or Navigate Page :  ");
            option = scanner.nextLine().toLowerCase();
            switch (option) {
                case "f" -> pageNumber = 1;
                case "p" -> {
                    if (pageNumber > 1) {
                        pageNumber--;
                    }
                }
                case "g" -> {
                    try {
                        System.out.print("> Enter Page Number : ");
                        int pageNo = Integer.parseInt(scanner.nextLine());
                        if (pageNo >= 1 && pageNo <= totalPage) {
                            pageNumber = pageNo;
                        } else {
                            System.out.println("Invalid page number.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid page number.");
                    }
                }
                case "n" -> {
                    if (pageNumber < totalPage) {
                        pageNumber++;
                    }
                }
                case "l" -> pageNumber = totalPage;
                case "b" -> isTrue = false;
                default -> System.out.println("Invalid Option.");
            }
        } while (isTrue);
    }

    @Override
    public void searchProductByName(List<Product> productList) {
        try {
            System.out.print("Enter product name or part of the name to search: ");
            String searchKeyword = scanner.nextLine().trim().toLowerCase();

            List<Product> matchingProducts = new ArrayList<>();

            for (Product product : productList) {
                if (product.getProductName().toLowerCase().contains(searchKeyword)) {
                    matchingProducts.add(product);
                }
            }

            if (!matchingProducts.isEmpty()) {
                displayAllProduct(matchingProducts, 1, pagination.setNewRow());
            } else {
                System.out.println("No products found matching the search criteria.");
            }
        } catch (Exception e) {
            Message.errMessage(e.getMessage());
        }
    }
}

