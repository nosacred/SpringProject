package main.model;

import com.google.gson.annotations.SerializedName;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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
    private int quantityNotInOrders;

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
    private String apiKey;

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

    public int getQuantityNotInOrders() {
        return quantityNotInOrders;
    }

    public void setQuantityNotInOrders(int quantityNotInOrders) {
        this.quantityNotInOrders = quantityNotInOrders;
    }


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
}
