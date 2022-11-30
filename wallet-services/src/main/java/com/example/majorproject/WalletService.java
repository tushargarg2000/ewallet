package com.example.majorproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;


    @KafkaListener(topics = {"create_wallet"}, groupId = "friends_group")
    public void createWallet(String message) throws JsonProcessingException {

        JSONObject walletRequest = objectMapper.readValue(message,JSONObject.class);

        String userName = (String) walletRequest.get("userName");


        Wallet wallet = Wallet.builder()
                .userName(userName)
                .balance(0).build();

        //We are saving the wallet Repository
        walletRepository.save(wallet);
    }

    @KafkaListener(topics = {"update_wallet"}, groupId = "friends_group")
    public void updateWallet(String message) throws JsonProcessingException {

        JSONObject walletRequest = objectMapper.readValue(message,JSONObject.class);

        String fromUser = (String) walletRequest.get("fromUser");
        String toUser = (String) walletRequest.get("toUser");

        int transactionAmount = (Integer)walletRequest.get("amount");

        String transactionId = (String)walletRequest.get("transactionId");



        //TODO STEPS :
        // 1st CHECK BALANCE FROM fromUser
        /*

            //IF FAIL (if senders balance is not sufficient)
            //SEND STATUS AS FAILED

            //OTHERWISE
            deduct the senders money
            add the receivers money
            SEND STATUS AS SUCCESS

         */

        Wallet sendersWallet = walletRepository.findByUserName(fromUser);

        if(sendersWallet.getBalance()>=transactionAmount){

            //HAPPY CASE

            //UPDATE THE WALLETS
            walletRepository.updateWallet(fromUser,-1*transactionAmount);
            walletRepository.updateWallet(toUser,transactionAmount);

        //PUSH TO KAFKA

        JSONObject sendToTransaction = new JSONObject();

        sendToTransaction.put("transactionId",transactionId);
        sendToTransaction.put("TransactionStaus","SUCCESS");

        String sendMessage =  sendToTransaction.toString();

        kafkaTemplate.send("update_transaction",sendMessage);

        }
        else{

            //SAD CASE
            JSONObject sendToTransaction = new JSONObject();

            sendToTransaction.put("transactionId",transactionId);
            sendToTransaction.put("TransactionStaus","FAILED");
            String sendMessage =  sendToTransaction.toString();

            kafkaTemplate.send("update_transaction",sendMessage);

        }

    }


}
