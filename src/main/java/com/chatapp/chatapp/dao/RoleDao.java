package com.chatapp.chatapp.dao;

import com.chatapp.chatapp.entity.Privilege;
import com.chatapp.chatapp.entity.Role;
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
public class RoleDao {

    private final JdbcTemplate jdbcTemplate;

    public Role findByName(String name) {
        String findRoleByNameSql = "SELECT * FROM role WHERE name = ?";

        try {
            return jdbcTemplate.queryForObject(findRoleByNameSql, new Object[]{name}, (rs, rowNum) ->
                    new Role(
                            rs.getLong("id"),
                            rs.getString("name")
                    ));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        catch (Exception e) {
            throw new RuntimeException("Role sql exception : " + e.getMessage());
        }
    }

    public Role findById(Long id) {
        String findRoleByIdSql = "SELECT * FROM role WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(findRoleByIdSql, new Object[]{id}, (rs, rowNum) ->
                    new Role(
                            rs.getLong("id"),
                            rs.getString("name")
                    ));
        } catch (EmptyResultDataAccessException e) {
            //throw new ResourceNotFoundException("Role not found with this name : " + name);
            return null;
        }
        catch (Exception e) {
            throw new RuntimeException("Role sql exception : " + e.getMessage());
        }
    }

    public Role save(Role role) {
        String sql = "INSERT INTO role (name) VALUES (?)";


        try {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(sql, new String [] {"id"});
                    statement.setString(1, role.getName());
                    return statement;
                }
            }, holder);

            long primaryKey = holder.getKey().longValue();
            role.setId(primaryKey);
        }  catch (Exception e) {
            throw new RuntimeException("Role sql exception : " + e.getMessage());
        }
        return role;
    }

    public void addPrivilegeToRole(Role role, Privilege privilege) {
        String sql = "INSERT INTO roles_privileges (role_id,privilege_id) VALUES (?,?)";

        try {
            jdbcTemplate.update(sql, role.getId(),privilege.getId()  );
        } catch (Exception e) {
            throw new RuntimeException("addPrivilegeToRole sql exception : " + e.getMessage());
        }
    }
}
