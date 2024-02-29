package view;

import model.Product;

import java.util.List;

public interface Menu {
    void displayBanner();
    void displayMainMenu();
    void displayHelp();
    void confirmation(List<Product> productList, String code);
}