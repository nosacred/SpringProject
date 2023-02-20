package main.model;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.util.Objects;


public class StockId implements Serializable {

    public StockId(String barcode, String  warehouseName) {
        this.barcode = barcode;
        this.warehouseName = warehouseName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockId stockId = (StockId) o;
        return getWarehouse().equals(stockId.getWarehouse()) && getBarcode().equals(stockId.getBarcode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBarcode(), getWarehouse());
    }

    public StockId() {
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getWarehouse() {
        return warehouseName;
    }

    public void setWarehouse(String warehouseName) {
        this.warehouseName = warehouseName;
    }
    private String barcode;
    private String warehouseName;

}
