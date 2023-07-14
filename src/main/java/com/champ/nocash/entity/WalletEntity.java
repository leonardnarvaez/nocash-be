package com.champ.nocash.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity(name="wallet")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userId;
    private BigDecimal balance = BigDecimal.valueOf(0);

    public void deposit(BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount should not be negative or zero");
        }
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
