package com.example.majorproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ObjectMapper objectMapper;


    public void createWallet(Wallet walletRequest) throws JsonProcessingException {


        Wallet wallet = Wallet.builder()
                .userName(walletRequest.getUserName())
                .amount(0).build();

        //We are saving the wallet Repository
        walletRepository.save(wallet);
    }


}
