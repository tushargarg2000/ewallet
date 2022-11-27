package com.example.majorproject;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Table(name = "wallets")
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String userName;

    int amount;



}
