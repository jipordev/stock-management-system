package service;

import filemethods.FileMethods;
import filemethods.FileMethodsImpl;
import model.Product;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import util.Pagination;
import util.PaginationImpl;

import java.time.LocalDate;
import java.util.*;

public class ProductServiceImpl implements ProductService {
    static Scanner scanner = new Scanner(System.in);
    private static final String DATA_SOURCE_FILE = "product.bak";
    private static final String TRANSFER_FILE = "transproduct.bak";
    public static FileMethods fileMethods = new FileMethodsImpl();
    public static Pagination pagination = new PaginationImpl();
    static Table tableUpdate = new Table(1, BorderStyle.UNICODE_BOX_DOUBLE_BORDER, ShownBorders.SURROUND);

    @Override
    public void randomRecord(List<Product> productList) {
        System.out.print("Enter amount of record : ");
        int randomNumber = Integer.parseInt(scanner.nextLine());
        Product[] products = new Product[randomNumber];
        for (int i = 0; i<randomNumber; i++) {
            products[i] = new Product();
            products[i].setProductCode("CSTAD"+ i);
            products[i].setProductName("Product::"+i);
            products[i].setProductPrice(0.0);
            products[i].setQty(0);
            products[i].setDate(LocalDate.now());
            products[i].setStatus("null");
        }
        productList.addAll(List.of(products));

        fileMethods.writeToFile(productList,DATA_SOURCE_FILE);
    }

    @Override
    public void createProduct(List<Product> productList) {
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
        fileMethods.writeTransferRecord(product,TRANSFER_FILE);

        System.out.println("New product created successfully.");
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
        productList = fileMethods.readProductsFromFile(TRANSFER_FILE);
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
    }

    @Override
    public void updateProduct(List<Product> productList) {
        Product updateProduct = new Product();
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
                for (Product product : productList) {
                    if (product.getProductCode().equals(code)) {
                        tableUpdate.addCell("Product code: "+product.getProductCode());
                        tableUpdate.addCell("Product name: "+product.getProductName());
                        tableUpdate.addCell("Product price: "+product.getProductPrice());
                        tableUpdate.addCell("Product quantity: "+product.getQty());
                        tableUpdate.addCell("Product date: "+product.getDate());
                        tableUpdate.addCell("Product status: "+product.getStatus());
                        System.out.println(tableUpdate.render());

                        System.out.print("Enter new NAME : ");
                        updateProduct.setProductName(scanner.nextLine());
                        System.out.print("Enter new PRICE : ");
                        updateProduct.setProductPrice(Double.parseDouble(scanner.nextLine()));
                        System.out.print("Enter new QTY : ");
                        updateProduct.setQty(Integer.parseInt(scanner.nextLine()));
                        updateProduct.setDate(LocalDate.now());
                        updateProduct.setStatus("update");
                        updateProduct.setProductCode(product.getProductCode());
                        productList.set(productList.indexOf(product), updateProduct);
                        fileMethods.writeTransferRecord(updateProduct,TRANSFER_FILE);
                    }
                }
            }
            case 2 -> {
                System.out.print("Enter product code: ");
                String proCode = scanner.nextLine();
                for (Product product : productList){
                    if (product.getProductCode().equals(proCode)){
                        tableUpdate.addCell("Product code: "+product.getProductCode());
                        tableUpdate.addCell("Product name: "+product.getProductName());
                        tableUpdate.addCell("Product price: "+product.getProductPrice());
                        tableUpdate.addCell("Product quantity: "+product.getQty());
                        tableUpdate.addCell("Product date: "+product.getDate());
                        tableUpdate.addCell("Product status: "+product.getStatus());
                        System.out.println(tableUpdate.render());

                        System.out.print("Enter new product name: ");
                        updateProduct.setProductName(scanner.nextLine());
                        updateProduct.setProductCode(product.getProductCode());
                        updateProduct.setProductPrice(product.getProductPrice());
                        updateProduct.setQty(product.getQty());
                        updateProduct.setDate(product.getDate());
                        updateProduct.setStatus(product.getStatus());
                        productList.set(productList.indexOf(product), updateProduct);
                        fileMethods.writeTransferRecord(updateProduct, TRANSFER_FILE);
                    }
                }
            }
            case 3 -> {
                System.out.print("Enter product code: ");
                String proCode = scanner.nextLine();
                for (Product product : productList){
                    if (product.getProductCode().equals(proCode)){
                        tableUpdate.addCell("Product code: "+product.getProductCode());
                        tableUpdate.addCell("Product name: "+product.getProductName());
                        tableUpdate.addCell("Product price: "+product.getProductPrice());
                        tableUpdate.addCell("Product quantity: "+product.getQty());
                        tableUpdate.addCell("Product date: "+product.getDate());
                        tableUpdate.addCell("Product status: "+product.getStatus());
                        System.out.println(tableUpdate.render());

                        System.out.print("Enter new product price: ");
                        updateProduct.setProductPrice(Double.parseDouble(scanner.nextLine()));
                        updateProduct.setProductName(product.getProductName());
                        updateProduct.setProductCode(product.getProductCode());
                        updateProduct.setQty(product.getQty());
                        updateProduct.setDate(product.getDate());
                        updateProduct.setStatus(product.getStatus());
                        productList.set(productList.indexOf(product), updateProduct);
                        fileMethods.writeTransferRecord(updateProduct, TRANSFER_FILE);
                    }
                }
            }
            case 4 -> {
                System.out.print("Enter product code: ");
                String proCode = scanner.nextLine();
                for (Product product : productList){
                    if (product.getProductCode().equals(proCode)){
                        tableUpdate.addCell("Product code: "+product.getProductCode());
                        tableUpdate.addCell("Product name: "+product.getProductName());
                        tableUpdate.addCell("Product price: "+product.getProductPrice());
                        tableUpdate.addCell("Product quantity: "+product.getQty());
                        tableUpdate.addCell("Product date: "+product.getDate());
                        tableUpdate.addCell("Product status: "+product.getStatus());
                        System.out.println(tableUpdate.render());

                        System.out.print("Enter new product qty: ");
                        updateProduct.setQty(Integer.parseInt(scanner.nextLine()));
                        updateProduct.setProductPrice(product.getProductPrice());
                        updateProduct.setProductName(product.getProductName());
                        updateProduct.setProductCode(product.getProductCode());
                        updateProduct.setDate(product.getDate());
                        updateProduct.setStatus(product.getStatus());
                        productList.set(productList.indexOf(product), updateProduct);
                        fileMethods.writeTransferRecord(updateProduct, TRANSFER_FILE);
                    }
                }
            }
            default -> System.out.println("Invalid update option");
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
                Product product = productList.get(i);
                table.addCell(product.getProductCode(), cellStyle);
                table.addCell(product.getProductName(), cellStyle);
                table.addCell(product.getProductPrice().toString(), cellStyle);
                table.addCell(product.getQty().toString(), cellStyle);
                table.addCell(product.getDate().toString(), cellStyle);
                table.addCell(product.getStatus(), cellStyle);
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
    public void searchProductByName() {
        List<Product> searchProducts = fileMethods.readProductsFromFile(TRANSFER_FILE);
        System.out.print("Enter product name or part of the name to search: ");
        String searchKeyword = scanner.nextLine().trim().toLowerCase();

        List<Product> matchingProducts = new ArrayList<>();

        for (Product product : searchProducts) {
            if (product.getProductName().toLowerCase().contains(searchKeyword)) {
                matchingProducts.add(product);
            }
        }

        if (!matchingProducts.isEmpty()) {
            displayAllProduct(matchingProducts, 1, pagination.setNewRow());
        } else {
            System.out.println("No products found matching the search criteria.");
        }
    }
}

