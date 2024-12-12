package org.example.model;

import java.math.BigDecimal;

public class SaleOrder {

    public String getSupplierArticle() {
        return supplierArticle;
    }

    public void setSupplierArticle(String supplierArticle) {
        this.supplierArticle = supplierArticle;
    }

    public String getTechSize() {
        return techSize;
    }

    public void setTechSize(String techSize) {
        this.techSize = techSize;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }


    public String getNmId() {
        return nmId;
    }

    public void setNmId(String nmId) {
        this.nmId = nmId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getForPay() {
        return forPay;
    }

    public void setForPay(BigDecimal forPay) {
        this.forPay = forPay;
    }

    public BigDecimal getCancelPay() {
        return cancelPay;
    }

    public void setCancelPay(BigDecimal cancelPay) {
        this.cancelPay = cancelPay;
    }

    public int getOrderCancel() {
        return orderCancel;
    }

    public void setOrderCancel(int orderCancel) {
        this.orderCancel = orderCancel;
    }

    public int getSaleCancel() {
        return saleCancel;
    }

    public void setSaleCancel(int saleCancel) {
        this.saleCancel = saleCancel;
    }

    public String getWbLink() {
        return wbLink;
    }

    public void setWbLink(String wbLink) {
        this.wbLink = wbLink;
    }

    private String supplierArticle;
    private String techSize;
    private String barcode;
    private BigDecimal totalPrice;
    private String nmId;
    private String subject;
    private String category;
    private String brand;
    private BigDecimal forPay;
    private BigDecimal cancelPay;
    private int ordersCount;
    private int orderCancel;
    private int saleCancel;

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    private int saleCount;
    private String wbLink;
    private BigDecimal totalLogistic;

    public BigDecimal getTotalLogistic() {
        return totalLogistic;
    }

    public void setTotalLogistic(BigDecimal totalLogistic) {
        this.totalLogistic = totalLogistic;
    }


    public int getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(int ordersCount) {
        this.ordersCount = ordersCount;
    }


}
