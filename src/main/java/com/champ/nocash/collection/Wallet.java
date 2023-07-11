package com.champ.nocash.collection;

import java.math.BigDecimal;

public class Wallet {
    private BigDecimal balance;
    public Wallet() {
        balance = BigDecimal.valueOf(0.0);
    }
    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(balance) >= 1) {
            throw new IllegalArgumentException("Insufficient Funds");
        }
        balance = balance.subtract(amount);
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
