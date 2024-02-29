package service;

import model.Product;

import java.util.List;

public interface ProductService {
    void randomRecord(List<Product> productList);
    void createProduct(List<Product> productList);
    void deleteProduct(List<Product> products);
    void readProduct(List<Product> productList);
    void updateProduct(List<Product> productList);
    void displayAllProduct(List<Product> productList, int pageNumber, int pageSize);
    void searchProductByName(List<Product> products);
//    void loadingInMain();
}