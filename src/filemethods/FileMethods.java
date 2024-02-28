package filemethods;

import model.Product;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface FileMethods {
    List<Product> readProductsFromFile(String fileName);

    void writeToFile(List<Product> productList, String fileName);
    String  backupFileDir();
    void writeTransferRecord(Product product, String transferFileName);
    boolean hasData(String fileName);
    void backUpData(String sourceFilePath, String backupFilePath);
    void restoreData();
    void listingBackupFiles();

}