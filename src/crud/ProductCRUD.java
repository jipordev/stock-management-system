package crud;

import model.Product;

import java.util.List;

public interface ProductCRUD {
    void searchProduct(List<Product> productList);
    void createProduct(Product product);
    void updateProduct(List<Product> productList);
    void readProduct(List<Product> productList);
    void deleteProduct(List<Product> productList);
}
