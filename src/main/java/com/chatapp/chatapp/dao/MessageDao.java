package com.chatapp.chatapp.dao;

import com.chatapp.chatapp.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class MessageDao {

    ZoneOffset zoneOffSet = ZoneOffset.of("+00:00");

   /* public Message save(Message message){
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneOffSet);
        String messageSaveSql = "INSERT INTO message (password,email,name,login_type,user_status,created_on," +
                "last_login)" +
                "VALUES " +
                "(?,?,?,?,?,?,?);";
    }*/

}
