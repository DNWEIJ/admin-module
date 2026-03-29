package dwe.holding.reporting.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Counter {
    public final static String name = "COUNTER";

    private int incrementValue = 0;
    private int maxValue = 0;
    private boolean finished = false;

    public Counter(int maxValue){
        this.maxValue = maxValue;
    }
    public void increment() {
        incrementValue++;
    }
}
