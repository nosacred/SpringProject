package org.example.model;

import com.google.gson.annotations.SerializedName;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@IdClass(StockId.class)
@Table(name="stocks")
public class Stock {

    private String lastChangeDate;
    private String supplierArticle;
    private String techSize;
    @Column(name = "barcode", updatable = false,insertable = false)
    @Id
    private String barcode;
    private int quantity;
    private String isSupply;
    private String isRealization;
    private int quantityFull;
    private int inWayToClient;

    public int getInWayToClient() {
        return inWayToClient;
    }

    public int getInWayFromClient() {
        return inWayFromClient;
    }

    private int inWayFromClient;


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Column(name = "warehouse_name", updatable = false,insertable = false)
    @Id
    private String warehouseName;
    private String nmId;
    private String subject;
    private String category;
    private int daysOnSite;
    private String brand;
    private String SCCode;
    @SerializedName("Price")
    private BigDecimal price;
    @SerializedName("Discount")
    private int discount;
    @Column(columnDefinition="text")
    private String apiKey;

    public String getWBLink(){

        return "https://www.wildberries.ru/catalog/"+ nmId + "/detail.aspx?targetUrl=BP";
    }

    public BigDecimal getTotalPriceWithDisc(){

        return price.multiply(BigDecimal.valueOf(100- discount)).divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    final static DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String getLastChangeDate() {
        LocalDateTime ldt =LocalDateTime.parse(lastChangeDate.substring(0,19), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        return ldt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }
    public LocalDateTime getLastChangeDateT() {
        LocalDateTime ldt =LocalDateTime.parse(lastChangeDate.substring(0,19), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        return ldt;
    }

    public void setLastChangeDate(String lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

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


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getIsSupply() {
        return isSupply;
    }

    public void setIsSupply(String isSupply) {
        this.isSupply = isSupply;
    }

    public String getIsRealization() {
        return isRealization;
    }

    public void setIsRealization(String isRealization) {
        this.isRealization = isRealization;
    }

    public int getQuantityFull() {
        return quantityFull;
    }

    public void setQuantityFull(int quantityFull) {
        this.quantityFull = quantityFull;
    }

//    public int getQuantityNotInOrders() {
//        return quantityNotInOrders;
//    }
//
//    public void setQuantityNotInOrders(int quantityNotInOrders) {
//        this.quantityNotInOrders = quantityNotInOrders;
//    }


    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
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

    public int getDaysOnSite() {
        return daysOnSite;
    }

    public void setDaysOnSite(int daysOnSite) {
        this.daysOnSite = daysOnSite;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSCCode() {
        return SCCode;
    }

    public void setSCCode(String SCCode) {
        this.SCCode = SCCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock)) return false;
        Stock stock = (Stock) o;
        return getWarehouseName().equals(stock.getWarehouseName())
                &&getBarcode().equals(stock.getBarcode())
                && getNmId().equals(stock.getNmId())
                && getSubject().equals(stock.getSubject())
                && getCategory().equals(stock.getCategory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWarehouseName(), getNmId(), getSubject(),getBarcode(), getCategory());
    }
}
