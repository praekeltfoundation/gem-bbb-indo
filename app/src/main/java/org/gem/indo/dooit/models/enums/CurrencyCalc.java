package org.gem.indo.dooit.models.enums;

/**
 * Created by Wimpie Victor on 2017/02/24.
 */

public enum CurrencyCalc {
    IDR("IDR", 100.0),
    ZAR("ZAR", 0.05);

    private final String code;

    /**
     * The closest number to round to. Usually represents the smallest coin a user has access to for
     * their currency.
     */
    private final double rounding;

    CurrencyCalc(String code, double rounding) {
        this.code = code;
        this.rounding = rounding;
    }

    /////////////
    // Getters //
    /////////////


    public String getCode() {
        return code;
    }

    public double getRounding() {
        return rounding;
    }

    //////////////
    // Rounding //
    //////////////

    public double ceil(double value) {
        return Math.ceil(value / rounding) * rounding;
    }

    public double floor(double value) {
        return Math.floor(value / rounding) * rounding;
    }

    public double round(double value) {
        return Math.round(value / rounding) * rounding;
    }
}
