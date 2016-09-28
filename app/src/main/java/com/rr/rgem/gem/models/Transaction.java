package com.rr.rgem.gem.models;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Rudolph Jacobs on 2016-09-19.
 */
public class Transaction {
    final Date date;
    final BigDecimal value;

    public Transaction(Date date, BigDecimal value) {
        this.date = date;
        this.value = value;
    }
}
