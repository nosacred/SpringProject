package main.model;

import com.google.gson.annotations.SerializedName;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@IdClass(StockId.class)
@Table(name="stocks")
public class Stock {

    private String lastChangeDate;
    private String supplierArticle;
    private String techSize;
//    @Column(name = "barcode", updatable = false,insertable = false)
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

    public int getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(int warehouse) {
        this.warehouse = warehouse;
    }

//    @Column(name = "warehouse", updatable = false,insertable = false)
    @Id
    private int warehouse;
    private String warehouseName;
    private int inWayToClient;
    private int inWayFromClient;
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

    public String getLastChangeDate() {
        return lastChangeDate;
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

    public int getInWayToClient() {
        return inWayToClient;
    }

    public void setInWayToClient(int inWayToClient) {
        this.inWayToClient = inWayToClient;
    }

    public int getInWayFromClient() {
        return inWayFromClient;
    }

    public void setInWayFromClient(int inWayFromClient) {
        this.inWayFromClient = inWayFromClient;
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
