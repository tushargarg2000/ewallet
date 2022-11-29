package com.example.majorproject;


import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
@Table(name = "wallets")
public class Wallet {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String userName;

    int amount;

}
