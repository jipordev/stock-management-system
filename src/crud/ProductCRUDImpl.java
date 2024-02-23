package crud;

import model.Product;

import java.util.List;
import java.util.Scanner;

public class ProductCRUDImpl implements ProductCRUD{
    static Scanner scanner = new Scanner(System.in);

    @Override
    public void searchProduct(List<Product> productList) {
        System.out.print("Enter product name");
        String proName = scanner.nextLine().toLowerCase();
        for (Product product : productList) {
            if (product.getProductName().contains(proName)){

            }
        }
    }

    @Override
    public void createProduct(Product product) {
        System.out.print("Enter product name : ");
        product.setProductName(scanner.nextLine());
        System.out.print("Enter unit price : ");
        product.setPrice(Double.parseDouble(scanner.nextLine()));
        System.out.print("Enter qty : ");
        product.setQty(Integer.parseInt(scanner.nextLine()));
    }

    @Override
    public void updateProduct(List<Product> productList) {
        System.out.print("Enter product name : ");
        String proName = scanner.nextLine();
        productList.stream().filter(product -> {
            if (product.getProductName().contains(proName)) {
                System.out.print("Enter new product name : ");
            }
            return false;
        }).forEach(System.out::println);
    }

    @Override
    public void readProduct(List<Product> productList) {

    }

    @Override
    public void deleteProduct(List<Product> productList) {

    }
}
