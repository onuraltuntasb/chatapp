package com.chatapp.chatapp.dao;


import com.chatapp.chatapp.entity.Friend;
import com.chatapp.chatapp.entity.Role;
import com.chatapp.chatapp.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

@Component
@RequiredArgsConstructor
public class FriendDao {

    private final JdbcTemplate jdbcTemplate;

    public Boolean save(Long userId,Long friendId){

        String friendSaveSql = "INSERT INTO friend(user_id,friend_id) VALUES(?,?);";


        try {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(friendSaveSql, new String[] {"id"});
                    statement.setInt(1,userId.intValue());
                    statement.setInt(2,friendId.intValue());

                    return statement;
                }
            }, holder);

        }  catch (Exception e) {
            throw new RuntimeException("Friend save sql exception : " + e.getMessage());
        }

        return true;
    }

    public Friend findByEmail(String email) {

        String findFriendByEmailSql = "SELECT * FROM friend WHERE email = ?";

        try {
            Friend rFriend =  jdbcTemplate.queryForObject(findFriendByEmailSql, new Object[]{email}, (rs, rowNum) ->
                    new Friend(
                            rs.getLong("id"),
                            rs.getLong("user_id"),
                            rs.getLong("friend_id")

                    ));

            return rFriend;
        } catch (EmptyResultDataAccessException e) {
            //throw new ResourceNotFoundException("Privilege not found with this name : " + name);
            return null;
        }
    }


}
