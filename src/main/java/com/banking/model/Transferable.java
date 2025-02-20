package com.banking.model;

import java.math.BigDecimal;

public interface Transferable {
    boolean transfer(Account destination, BigDecimal amount);

    default boolean canTransfer(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
