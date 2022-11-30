package com.example.majorproject;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;


    private String transactionId = UUID.randomUUID().toString();

    private String fromUser;

    private String toUser;

    private int amount;

    private TransactionStatus status;

    private String transactionTime;

}
