/**
 * * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * The cart item business object.
 */
@Entity
public class Item implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long sId;

    @JsonProperty("id")
    private String id;

    @NotNull(message = "Item name must not be null")
    private String itemId;

    private int quantity;
    private float unitPrice;

    public Item() {
        this(null, "", 1, 0F);
    }

    public Item(String id,
                @NotNull(message = "Item Id must not be null") String itemId,
                int quantity,
                float unitPrice) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * @return the sId
     */
    public Long getsId() {
        return sId;
    }

    /**
     * @param sId the sId to set
     */
    public void setsId(Long sId) {
        this.sId = sId;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the unitPrice
     */
    public float getUnitPrice() {
        return unitPrice;
    }

    /**
     * @param unitPrice the unitPrice to set
     */
    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public float getTotal() {
        return unitPrice * quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Item item = (Item) o;
        return quantity == item.quantity &&
                Float.compare(item.unitPrice, unitPrice) == 0 &&
                Objects.equals(id, item.id) &&
                Objects.equals(itemId, item.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemId, quantity, unitPrice);
    }

    @Override
    public String toString() {
        return "Item{" +
                "sId=" + sId +
                ", id='" + id + '\'' +
                ", itemId='" + itemId + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
