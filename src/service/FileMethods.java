package service;

import model.Product;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface FileMethods {
    List<Product> readProductsFromFile(String fileName);

    void writeToFile(List<Product> productList, String fileName);
    void updateRecord(List<Product> productList, String dataSourceFileName, String transferFileName);
    String  backupFileDir();
    void writeTransferRecord(Product product, String transferFileName);
    boolean hasData(String fileName);
    void backUpData(String sourceFilePath, String backupFilePath);
    void restoreData();
    void listingBackupFiles();

    void checkFileForCommit(List<Product> productList);
    void clearFileTransfer(String TRANSFER_FILE);
    void destroy(String TRANSFER_FILE);
    void displayCommit(List<Product> transferProduct);

}