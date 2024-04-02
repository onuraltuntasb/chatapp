package com.chatapp.chatapp.dao;

import com.chatapp.chatapp.entity.Privilege;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class PrivilegeDao {

    private final JdbcTemplate jdbcTemplate;

    public Privilege findByName(String name) {

        String sql = "SELECT * FROM privilege WHERE name = ?;";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{name}, (rs, rowNum) ->
                    new Privilege(
                            rs.getLong("id"),
                            rs.getString("name")
                    ));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Privilege save(Privilege privilege) {
        String sql = "INSERT INTO privilege (name) VALUES (?);";


        try {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(sql, new String [] {"id"});
                    statement.setString(1, privilege.getName());
                    return statement;
                }
            }, holder);

            long primaryKey = holder.getKey().longValue();
            privilege.setId(primaryKey);
        }  catch (Exception e) {
            throw new RuntimeException("Privilege sql exception : " + e.getMessage());
        }
        return privilege;
    }

}
