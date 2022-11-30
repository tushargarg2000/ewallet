package com.example.majorproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;


@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;


    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public final String REDIS_PREFIX_KEY = "user::"; //optional

    public final String CREATE_WALLET_TOPIC = "create_wallet";

    public void createUser(UserRequest userRequest) {



        //Goal is to save the User into the Cache

        User user = User.builder()
                .age(userRequest.getAge())
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .userName(userRequest.getUserName())
                .build();



        userRepository.save(user);

        //SAVING IN THE CACHE
        saveInCache(user);


        //Sending a message through Kafka :
        JSONObject jsonObject = new JSONObject();


        jsonObject.put("userName",user.getUserName());
        jsonObject.put("name",user.getName());


        //Converting jsonObject to String bcz message is in string format
        String message = jsonObject.toString();

        kafkaTemplate.send("create_wallet",message);

    }

    private void saveInCache(User user){

        Map map = objectMapper.convertValue(user,Map.class);
        redisTemplate.opsForHash().putAll(REDIS_PREFIX_KEY+user.getUserName(),map);
        redisTemplate.expire(REDIS_PREFIX_KEY+user.getUserName(), Duration.ofHours(12));

    }


    public User getUserByUserName(String userName) throws Exception{

        //1. Find in cache

        Map map = redisTemplate.opsForHash().entries(REDIS_PREFIX_KEY+userName);

        if(map==null || map.size()==0){

            //Find in the DB
            User user = userRepository.findByUserName(userName);

            //If found
            if(user!=null){
                saveInCache(user);
            }
            else { //Throw an error
                throw new UserNotFoundException();
            }
            return user;

        }else{
            //Found in the cache --> return from the cache
            User object = objectMapper.convertValue(map,User.class);
            return object;

        }

    }
}
