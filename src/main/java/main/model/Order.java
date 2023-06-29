package main.model;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    private ZonedDateTime date;
    private ZonedDateTime lastChangeDate;

    public ZonedDateTime getCancel_dt() {
        return cancel_dt;
    }

    public void setCancel_dt(ZonedDateTime cancel_dt) {
        this.cancel_dt = cancel_dt;
    }

    private ZonedDateTime cancel_dt;
    private String supplierArticle;
    private String techSize;
    private String barcode;
    private BigDecimal totalPrice;
    private int discountPercent;
    private String warehouseName;
    private String oblast;
    private String incomeID;
    @Id
    private String odid;
    private String nmId;
    private String subject;
    private String category;
    private String brand;
    private String isCancel;
    private String gNumber;
    private String sticker;
    private String srid;
    private String apiKey;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    private String orderType;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public ZonedDateTime getLastChangeDate() {
        return lastChangeDate;
    }

    public void setLastChangeDate(ZonedDateTime lastChangeDate) {
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

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getOblast() {
        return oblast;
    }

    public void setOblast(String oblast) {
        this.oblast = oblast;
    }

    public String getIncomeID() {
        return incomeID;
    }

    public void setIncomeID(String incomeID) {
        this.incomeID = incomeID;
    }

    public String getOdid() {
        return odid;
    }

    public void setOdid(String odid) {
        this.odid = odid;
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

    public String getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(String isCancel) {
        this.isCancel = isCancel;
    }

    public String getgNumber() {
        return gNumber;
    }

    public void setgNumber(String gNumber) {
        this.gNumber = gNumber;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public String getSrid() {
        return srid;
    }

    public void setSrid(String srid) {
        this.srid = srid;
    }

    public BigDecimal getTotalPriceWithDisc(){

        return totalPrice.multiply(BigDecimal.valueOf(100- discountPercent)).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
    }

    public String getWBLink(){

        return "https://www.wildberries.ru/catalog/"+ nmId + "/detail.aspx?targetUrl=BP";
    }

    public BigDecimal getLogisticPrice(){
        BigDecimal cancel =BigDecimal.valueOf(50);
        BigDecimal logistic;
        switch (warehouseName){
            case ("МЛП-Подольск") :
            case ("Подольск") :
                logistic = BigDecimal.valueOf(83.5);break;
            case "Краснодар 2" :
            case "Краснодар" :
                logistic = BigDecimal.valueOf(47.5);break;
            case "Казань" : logistic =BigDecimal.valueOf(41.5);break;
            case "Санкт-Петербург 2" : logistic = BigDecimal.valueOf(30);break;
            case "Санкт-Петербург" : logistic = BigDecimal.valueOf(30);break;
            case "Электросталь" : logistic = BigDecimal.valueOf(42.5);break;
            case "Екатеринбург" : logistic = BigDecimal.valueOf(100);break;
            case "Хабаровск" : logistic = BigDecimal.valueOf(80);break;
            case "Новосибирск" : logistic = BigDecimal.valueOf(73.5);break;
            case "Алексин" : logistic = BigDecimal.valueOf(35);break;

            default:  logistic = BigDecimal.valueOf(69);break;
            }
            if(isCancel.equals("true")) logistic =  logistic.add(cancel);
            return logistic;
        }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return getOdid().equals(order.getOdid()) && getNmId().equals(order.getNmId()) && getSubject().equals(order.getSubject()) && getCategory().equals(order.getCategory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOdid(), getNmId(), getSubject(), getCategory());
    }
}
