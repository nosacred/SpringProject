package org.example.model;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;


public class OrderSum implements Comparable{
    private String name;
    private String WBArticle;
    private String barcode;
    private LocalDate orderDate;
    private int orderQuantity;
    private BigDecimal totalOrdersPriceSum;
    private String category;
    private String subject;
    private String link;
    private String brand;

    public String getTechSize() {
        return techSize;
    }

    public void setTechSize(String techSize) {
        this.techSize = techSize;
    }

    private String techSize;
    private int cancelCount = 0;
    private BigDecimal percentBackOrder;
    private double totalLogisticPrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWBArticle() {
        return WBArticle;
    }

    public void setWBArticle(String WBArticle) {
        this.WBArticle = WBArticle;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public BigDecimal getTotalOrdersPriceSum() {
        return totalOrdersPriceSum;
    }

    public void setTotalOrdersPriceSum(BigDecimal totalOrdersPriceSum) {
        this.totalOrdersPriceSum = totalOrdersPriceSum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getCancelCount() {
        return cancelCount;
    }

    public void setCancelCount(int cancelCount) {
        this.cancelCount = cancelCount;
    }

    public void setPercentBackOrder(BigDecimal percentBackOrder) {
        this.percentBackOrder = percentBackOrder;
    }

    public double getTotalLogisticPrice() {
        return totalLogisticPrice;
    }

    public void setTotalLogisticPrice(double totalLogisticPrice) {
        this.totalLogisticPrice = totalLogisticPrice;
    }

    public OrderSum(ArrayList<Order> ordersPerBarCode) throws IOException {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.name = ordersPerBarCode.get(0).getSupplierArticle();
        this.barcode = ordersPerBarCode.get(0).getBarcode();
        this.orderDate = ordersPerBarCode.get(0).getDate().toLocalDate();
        this.orderQuantity = ordersPerBarCode.size();
        this.totalOrdersPriceSum = getTotalOrdersPrice(ordersPerBarCode);
        this.category = ordersPerBarCode.get(0).getCategory();
        this.subject = ordersPerBarCode.get(0).getSubject();
        this.link = ordersPerBarCode.get(0).getWBLink();
        this.WBArticle = ordersPerBarCode.get(0).getNmId();
        getCancelCount(ordersPerBarCode);
        getPercentBackOrder();
        this.totalLogisticPrice = getTotalLogisticPrice(ordersPerBarCode);
        this.brand = ordersPerBarCode.get(0).getBrand();
        this.techSize = ordersPerBarCode.get(0).getTechSize();


    }
    private void getPercentBackOrder(){
        BigDecimal cancel = new BigDecimal(cancelCount);
        BigDecimal ordersQuantity = new BigDecimal(orderQuantity);
        BigDecimal sto = new BigDecimal("100");
        if(!ordersQuantity.equals(BigDecimal.ZERO)){
            BigDecimal a =  cancel.divide(ordersQuantity,2, RoundingMode.HALF_UP);
            percentBackOrder= a.multiply(sto).setScale(2,RoundingMode.HALF_UP);
        }
        else  percentBackOrder= BigDecimal.ZERO;

    }

    private void getCancelCount(ArrayList<Order> orders){
        for(Order order : orders){
            if(order.getIsCancel().equals("true")){
                cancelCount++;
            }
        }
    }
    private BigDecimal getTotalOrdersPrice(ArrayList<Order> orders){
        BigDecimal totalSum = new BigDecimal("0");
        for (Order order : orders) {
            totalSum= totalSum.add(order.getTotalPriceWithDisc()).setScale(2, RoundingMode.HALF_UP);
        }
        return  totalSum;
    }

    private double getTotalLogisticPrice(ArrayList<Order> ordersPerBarCode) throws IOException {

        String whName;
        double sum =0;
        double s=0;
        for(Order o : ordersPerBarCode){
            switch(o.getWarehouseName()){
                case ("МЛП-Подольск"):
                    whName ="podolsk";
                    s = 80;
                    break;
                case ("Электросталь"):
                    whName = "electrostal";
                    s = 57.5;
                    break;
                case ("Краснодар 2"):
                    whName = "krasnodar";
                    s = 22.5;
                    break;
                case ("Санкт-Петербург"):
                    whName = "spb";
                    s = 40;
                    break;
                case ("Новосибирск"):
                    whName = "novosib";
                    s = 60;
                    break;
                case("Санкт-Петербург 2"):
                    whName = "spb2";
                    s= 25;
                    break;
                default:whName = "koledino";
                    s = 72.5;
                    break;

            }

            if(o.getIsCancel().equals("true")) sum+=33;
            sum+=s;
        }
        return sum;
    }

    @Override
    public String toString() {
        return "OrderSum{" +
                "name='" + name + '\'' +
                ", WBArticle='" + WBArticle + '\'' +
                ", barcode='" + barcode + '\'' +
                ", orderDate=" + orderDate +
                ", orderQuantity=" + orderQuantity +
                ", totalOrdersPrice=" + totalOrdersPriceSum +
                ", category='" + category + '\'' +
                ", subject='" + subject + '\'' +
                ", link='" + link + '\'' +
                ", brand='" + brand + '\'' +
                ", techSize='" + techSize + '\'' +
                ", cancelCount=" + cancelCount +
                ", percentBackOrder=" + percentBackOrder +
                ", totalLogisticPrice=" + totalLogisticPrice +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        OrderSum orderSum = (OrderSum) o;
        return Integer.compare(orderQuantity,((OrderSum) o).orderQuantity);
    }
}

