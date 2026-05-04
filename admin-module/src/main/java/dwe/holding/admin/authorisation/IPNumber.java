package dwe.holding.admin.authorisation;


import dwe.holding.admin.exception.SystemException;
import lombok.Getter;

@Getter
public class IPNumber {
    private Integer numberOne;
    private Integer numberTwo;
    private Integer numberThree;
    private Integer numberFour;

    public IPNumber(){}

    public IPNumber(Integer numberOne, Integer numberTwo, Integer numberThree, Integer numberFour) {
        this.numberOne = numberOne;
        this.numberTwo= numberTwo;
        this.numberThree = numberThree;
        this.numberFour = numberFour;
        if (!validate()) throw new IllegalArgumentException("not a correct ip number");
    }

    public IPNumber(String ipnumber) {
        this.fromString(ipnumber);
    }
    public final String toString() {
        if (validate()) {
            return numberOne + "." + numberTwo + "." + numberThree + "." + numberFour;
        } else return null;
    }

    public final void fromString(String IPNumber) {
        String[] splitted = IPNumber.split("\\.");
        if (splitted.length != 4) {
            throw new SystemException(this.getClass().getName() + ": Not a correct IPNumber!!");
        }
        numberOne = Integer.valueOf(splitted[0]);
        numberTwo = Integer.valueOf(splitted[1]);
        numberThree = Integer.valueOf(splitted[2]);
        numberFour = Integer.valueOf(splitted[3]);
    }

    public boolean validate() {
        return (numberOne != null) && (numberOne <= 255) &&
                (numberTwo != null) && (numberTwo <= 255) &&
                (numberThree != null) && (numberThree <= 255) &&
                (numberFour != null) && (numberFour <= 255);
    }
}