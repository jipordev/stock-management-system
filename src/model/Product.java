package model;

import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;

public class Product {
    private String productCode;
    private String productName;
    private Integer qty;
    private Double price;
    private LocalDate localDate;

    public Product(){

    }

    public Product(String productCode, String productName, Integer qty, Double price, LocalDate localDate) {
        this.productCode = productCode;
        this.productName = productName;
        this.qty = qty;
        this.price = price;
        this.localDate = localDate;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
}
