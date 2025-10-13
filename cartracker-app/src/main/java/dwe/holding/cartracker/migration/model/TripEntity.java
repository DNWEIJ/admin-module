package dwe.holding.cartracker.migration.model;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(name = "car_entity")
@Deprecated
public class TripEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
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

    private TripEntity(Builder builder) {
        setId(builder.id);
        setDriveDate(builder.driveDate);
        setCarType(builder.carType);
        setPerson(builder.person);
        setKmTotal(builder.kmTotal);
        setKm(builder.km);
        setPetrol(builder.petrol);
        setLiters(builder.liters);
        setAmount(builder.amount);
    }

    public TripEntity() {

    }

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
//        return !driveDate.isEmpty()
//                && !carType.isEmpty()
//                && !person.isEmpty()
//                && kmTotal != 0
//                && km != 0;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDriveDate() {
        return driveDate;
    }

    public void setDriveDate(String driveDate) {
        this.driveDate = driveDate;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public int getKmTotal() {
        return kmTotal;
    }

    public void setKmTotal(int kmTotal) {
        this.kmTotal = kmTotal;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public boolean getPetrol() {
        return petrol;
    }

    public void setPetrol(boolean petrol) {
        this.petrol = petrol;
    }

    public int getLiters() {
        return liters;
    }

    public void setLiters(int liters) {
        this.liters = liters;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public static final class Builder {
        private Long id;
        private String driveDate;
        private String carType;
        private String person;
        private int kmTotal;
        private int km;
        private boolean petrol;
        private int liters;
        private int amount;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder driveDate(String driveDate) {
            this.driveDate = driveDate;
            return this;
        }

        public Builder carType(String carType) {
            this.carType = carType;
            return this;
        }

        public Builder person(String person) {
            this.person = person;
            return this;
        }

        public Builder kmTotal(int kmTotal) {
            this.kmTotal = kmTotal;
            return this;
        }

        public Builder km(int km) {
            this.km = km;
            return this;
        }

        public Builder petrol(boolean petrol) {
            this.petrol = petrol;
            return this;
        }

        public Builder liters(int liters) {
            this.liters = liters;
            return this;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public TripEntity build() {
            return new TripEntity(this);
        }
    }
}