package com.chatapp.chatapp.dao;

import com.chatapp.chatapp.entity.RefreshToken;
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
import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
public class RefreshTokenDao {

    private final JdbcTemplate jdbcTemplate;

    public RefreshToken findByToken(String token){
        String findByTokenSql = "SELECT * FROM refresh_token WHERE token = ?";

        try {
            return jdbcTemplate.queryForObject(findByTokenSql, new Object[]{token}, (rs, rowNum) ->
                    new RefreshToken(
                            rs.getLong("id"),
                            rs.getString("token"),
                            rs.getTimestamp("expiry_date").toInstant(),
                            rs.getLong("user_id")
                    ));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        catch (Exception e) {
            throw new RuntimeException("Refresh token sql exception : " + e.getMessage());
        }
    }

    public RefreshToken findTokenByUser(User user){
        String findTokenByUserSql = "SELECT * FROM refresh_token WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(findTokenByUserSql, new Object[]{user.getId()}, (rs, rowNum) ->
                    new RefreshToken(
                            rs.getLong("id"),
                            rs.getString("token"),
                            rs.getTimestamp("expiry_date").toInstant(),
                            rs.getLong("user_id")
                    ));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        catch (Exception e) {
            throw new RuntimeException("Refresh token sql exception : " + e.getMessage());
        }
    }


    public RefreshToken save(RefreshToken refreshToken) {
        String refreshTokenSaveSql = "INSERT INTO refresh_token (expiry_date,token,user_id) VALUES (?,?,?)";


        try {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(refreshTokenSaveSql, new String [] {"id"});
                    statement.setTimestamp(1, Timestamp.from(refreshToken.getExpiryDate()));
                    statement.setString(2,refreshToken.getToken());
                    statement.setLong(3,refreshToken.getUser_id());
                    return statement;
                }
            }, holder);

            long primaryKey = holder.getKey().longValue();
            refreshToken.setId(primaryKey);
        }  catch (Exception e) {
            throw new RuntimeException("refreshToken sql exception : " + e.getMessage());
        }
        return refreshToken;
    }

    public void deleteByToken(RefreshToken refreshToken){
        String deleteRefreshTokenSql = "delete from refresh_token where token = ?";
        try {
            jdbcTemplate.update(deleteRefreshTokenSql, refreshToken.getToken());
        }  catch (Exception e) {
            throw new RuntimeException("deleteByUserId refresh token delete sql exception : " + e.getMessage());
        }
    }

    public void deleteByUserId(Long userId){
        String deleteRefreshTokenSql = "delete from refresh_token where user_id = ?";
        try {
            jdbcTemplate.update(deleteRefreshTokenSql,userId);
        }  catch (Exception e) {
            throw new RuntimeException("deleteByUserId refresh token delete sql exception : " + e.getMessage());
        }
    }

    public RefreshToken updateByUserId(Long userId,RefreshToken refreshToken){

        String updateRefreshTokenSql = "update refresh_token set expiry_date = ? , token = ? where user_id = ?";
        try {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(updateRefreshTokenSql, new String [] {"id"});
                    statement.setTimestamp(1, Timestamp.from(refreshToken.getExpiryDate()));
                    statement.setString(2,refreshToken.getToken());
                    statement.setLong(3,userId);
                    return statement;
                }
            }, holder);

            long primaryKey = holder.getKey().longValue();
            refreshToken.setId(primaryKey);
        }  catch (Exception e) {
            throw new RuntimeException("refreshToken sql exception : " + e.getMessage());
        }
        return refreshToken;
    }

}
