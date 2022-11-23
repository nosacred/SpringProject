package main.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

@Entity
@Table(name = "sales")
public class Sale {

    public String getgNumber() {
        return gNumber;
    }

    public void setgNumber(String gNumber) {
        this.gNumber = gNumber;
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

    public BigDecimal getPromoCodeDiscount() {
        return promoCodeDiscount;
    }

    public void setPromoCodeDiscount(BigDecimal promoCodeDiscount) {
        this.promoCodeDiscount = promoCodeDiscount;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getOblastOkrugName() {
        return oblastOkrugName;
    }

    public void setOblastOkrugName(String oblastOkrugName) {
        this.oblastOkrugName = oblastOkrugName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getIncomeID() {
        return incomeID;
    }

    public void setIncomeID(int incomeID) {
        this.incomeID = incomeID;
    }

    public String getSaleID() {
        return saleID;
    }

    public void setSaleID(String saleID) {
        this.saleID = saleID;
    }

    public String getOdid() {
        return odid;
    }

    public void setOdid(String odid) {
        this.odid = odid;
    }

    public BigDecimal getSpp() {
        return spp;
    }

    public void setSpp(BigDecimal spp) {
        this.spp = spp;
    }

    public BigDecimal getForPay() {
        return forPay;
    }

    public void setForPay(BigDecimal forPay) {
        this.forPay = forPay;
    }

    public BigDecimal getFinishedPrice() {
        return finishedPrice;
    }

    public void setFinishedPrice(BigDecimal finishedPrice) {
        this.finishedPrice = finishedPrice;
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

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    private String gNumber;
    private ZonedDateTime date;
    private ZonedDateTime lastChangeDate;
    private String supplierArticle;
    private String techSize;
    private String barcode;
    private BigDecimal totalPrice;
    private int discountPercent;
    private String isSupply;
    private String isRealization;
    private BigDecimal promoCodeDiscount;
    private String warehouseName;
    private String countryName;
    private String oblastOkrugName;
    private String regionName;
    int incomeID;
    private String saleID;
    @Id
    private String odid;
    private BigDecimal spp;
    private BigDecimal forPay;
    private BigDecimal finishedPrice;
    private String nmId;
    private String subject;
    private String category;
    private String brand;
    private String sticker;

    BigDecimal getPriceWithDisc(){
        BigDecimal discount = new BigDecimal(100-discountPercent).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
        BigDecimal promoDiscount = new BigDecimal(100).subtract(promoCodeDiscount).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
        BigDecimal sppDiscount = new BigDecimal(100).subtract(spp).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);

        return totalPrice.multiply(discount).multiply(promoDiscount).multiply(sppDiscount).setScale(2,RoundingMode.HALF_UP);
    }
    String getWBLink(){

        return "https://www.wildberries.ru/catalog/"+ nmId + "/detail.aspx?targetUrl=BP";
    }
}
