package service;

import model.Product;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface ProductService {
    void randomRecord(List<Product> productList);
    void createProduct(List<Product> productList);
    void deleteProduct(List<Product> products);
    void readProduct(List<Product> productList);
    void updateProduct(List<Product> productList);
    void displayAllProduct(List<Product> productList, int pageNumber, int pageSize);
    void searchProductByName();

//    void loadingInMain();
}