package main.model;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.util.Objects;


public class StockId implements Serializable {

    public StockId(String barcode, int warehouse) {
        this.barcode = barcode;
        this.warehouse = warehouse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockId stockId = (StockId) o;
        return getWarehouse() == stockId.getWarehouse() && getBarcode().equals(stockId.getBarcode());
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

    public int getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(int warehouse) {
        this.warehouse = warehouse;
    }
    private String barcode;
    private int warehouse;

}
