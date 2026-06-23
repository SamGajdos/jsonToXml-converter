package io.github.samGajdos.converter;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.math.BigDecimal;

public class MessageDto {

    @NotBlank(message = "attribute 'id' is Empty\n")
    private String id;

    private String type;
    
    private String created;
    
    @NotNull(message = "attribute 'amount' is Empty\n")
    private BigDecimal amount;
    
    @Min(0)
    @Max(100)
    private int vat;

    private BigDecimal amountWithVat;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getVat() {
        return vat;
    }

    public void setVat(int vat) {
        this.vat = vat;
    }

    public BigDecimal getAmountWithVat() {
        return amountWithVat;
    }

    public void setAmountWithVat(BigDecimal amountWithVat) {
        this.amountWithVat = amountWithVat;
    }

    public String toString() { 
        return "id: '" + this.id + "', type: '" + this.type + 
               "', created: '" + this.created + "', amount: '" + this.amount +
               "', vat: '" + this.vat + "'";
    } 
}
