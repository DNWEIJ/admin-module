package dwe.holding.salesconsult.sales.controller;

public enum SalesType {
    OTC,
    PRICE_INFO,
    VISIT,
    ESTIMATE;


    public boolean isOtc() {
        return this.equals(OTC);
    }

    public boolean isPriceInfo() {
        return this.equals(PRICE_INFO);
    }

    public boolean isVisit() {
        return this.equals(VISIT);
    }

    public boolean isEstimate() {
        return this.equals(ESTIMATE);
    }
}