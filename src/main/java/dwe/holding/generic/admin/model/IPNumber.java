package dwe.holding.generic.admin.model;


import dwe.holding.generic.admin.exception.SystemException;

public class IPNumber {
    private Integer number1;
    private Integer number2;
    private Integer number3;
    private Integer number4;

    public final String toString() {
        if (validate()) {
            return number1 + "." + number2 + "." + number3 + "." + number4;
        } else return null;
    }

    public final void fromString(String IPNumber) {
        String[] splitted = IPNumber.split(".");
        if (splitted.length != 4) {
            throw new SystemException(this.getClass().getName() + ": Not a correct IPNumber!!");
        }
        number1 = Integer.valueOf(splitted[0]);
        number2 = Integer.valueOf(splitted[1]);
        number3 = Integer.valueOf(splitted[2]);
        number4 = Integer.valueOf(splitted[3]);
    }

    public boolean validate() {
        return (number1 != null) && (number1 <= 255) &&
                (number2 != null) && (number2 <= 255) &&
                (number3 != null) && (number3 <= 255) &&
                (number4 != null) && (number4 <= 255);
    }
}