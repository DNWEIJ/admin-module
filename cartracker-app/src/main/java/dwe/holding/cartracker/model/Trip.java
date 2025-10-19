package dwe.holding.cartracker.model;


import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "CARTRACKER_TRIP")
 @SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Trip extends TenantBaseBO {
    @Transient
    LocalDateTime driveDateLocalDate;
    String driveDate;
    String carType;
    String person;
    int kmTotal;
    int km;
    @Column(name="petrol",columnDefinition = "bit default 0")
    boolean petrol;
    @Column(name="liters",columnDefinition = "integer default 0")
    int liters;
    @Column(name="amount",columnDefinition = "integer default 0")
    int amount;

    public LocalDate getDriveDateLocalDate() {
        DateTimeFormatter daterFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(driveDate, daterFormatter);
    }

    @Override
    public String toString() {
        return driveDate + ";" + carType + ";" + person + ";" + kmTotal + ";" + km + ";" + petrol + ";"+ liters + ";" + amount + ";";
    }

    public String toHtmlString() {
        if (petrol) {
            return person + " drove:<br/>" + carType + " at " + driveDate + "<br/>for: " + km + " km and tanked: " + liters + "for " + amount;
        }
        return person + " drove:<br/>" + carType + " at " + driveDate + "<br/>for: " + km + " km";
    }

    public boolean isValid() {
        return true;
    }

    @Transient
    public String getLitersStr() {
        return (liters == 0 ? "" : Integer.toString(liters));
    }
    public void setLitersStr(String liters) {
        this.liters = (liters.isEmpty() ? 0 : Integer.parseInt(liters));
    }

    @Transient
    public String getAmountStr() {
        return (amount == 0 ? "" : Integer.toString(amount));
    }
    public void setAmountStr(String amount) {
        this.amount = (amount.isEmpty() ? 0 : Integer.parseInt(amount));
    }
}