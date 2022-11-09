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
import java.io.Serializable;
import java.util.Objects;

/**
 * The customer payment card business object.
 */
@Entity
public class Card implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long cardId;

    @JsonProperty("id")
    private String id;

    private String longNum;
    private String expires;
    private String ccv;

    public Card() {
    }

    public Card(String id, String longNum, String expires, String ccv) {
        this.id = id;
        this.longNum = longNum;
        this.expires = expires;
        this.ccv = ccv;
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
     * @return the cardId
     */
    public Long getCardId() {
        return cardId;
    }

    /**
     * @param cardId the cardId to set
     */
    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    /**
     * @return the longNum
     */
    public String getLongNum() {
        return longNum;
    }

    /**
     * @param longNum the longNum to set
     */
    public void setLongNum(String longNum) {
        this.longNum = longNum;
    }

    /**
     * @return the expires
     */
    public String getExpires() {
        return expires;
    }

    /**
     * @param expires the expires to set
     */
    public void setExpires(String expires) {
        this.expires = expires;
    }

    /**
     * @return the ccv
     */
    public String getCcv() {
        return ccv;
    }

    /**
     * @param ccv the ccv to set
     */
    public void setCcv(String ccv) {
        this.ccv = ccv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Card card = (Card) o;
        return Objects.equals(id, card.id) &&
                Objects.equals(longNum, card.longNum) &&
                Objects.equals(expires, card.expires) &&
                Objects.equals(ccv, card.ccv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, longNum, expires, ccv);
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardId=" + cardId +
                ", id='" + id + '\'' +
                ", longNum='" + longNum + '\'' +
                ", expires='" + expires + '\'' +
                ", ccv='" + ccv + '\'' +
                '}';
    }
}
