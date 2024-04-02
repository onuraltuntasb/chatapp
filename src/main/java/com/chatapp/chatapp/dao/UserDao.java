package com.chatapp.chatapp.dao;

import com.chatapp.chatapp.entity.Role;
import com.chatapp.chatapp.entity.User;
import com.chatapp.chatapp.payload.response.UsersRolesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final RoleDao roleDao;

    ZoneOffset zoneOffSet = ZoneOffset.of("+00:00");

    public User findByName(String name) {

        String findUserByNameSql = "SELECT * FROM users WHERE name = ?";

        try {
            User rUser =  jdbcTemplate.queryForObject(findUserByNameSql, new Object[]{name}, (rs, rowNum) ->
                    new User(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            User.UserStatus.valueOf(rs.getString("user_status"))

                    ));
            rUser.setRoles(findUserRoles(rUser));
            return rUser;
        } catch (EmptyResultDataAccessException e) {
            //throw new ResourceNotFoundException("Privilege not found with this name : " + name);
            return null;
        }
    }

    public User findByEmail(String email) {

        String findUserByEmailSql = "SELECT * FROM users WHERE email = ?";

        try {
            User rUser =  jdbcTemplate.queryForObject(findUserByEmailSql, new Object[]{email}, (rs, rowNum) ->
                    new User(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            User.UserStatus.valueOf(rs.getString("user_status"))

                    ));
            rUser.setRoles(findUserRoles(rUser));
            return rUser;
        } catch (EmptyResultDataAccessException e) {
            //throw new ResourceNotFoundException("Privilege not found with this name : " + name);
            return null;
        }
    }

    public User findById(Long id) {
        String findUserByIdSql = "SELECT * FROM users WHERE id = ?";

        try {
            User rUser =  jdbcTemplate.queryForObject(findUserByIdSql, new Object[]{id}, (rs, rowNum) ->
                    new User(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            User.UserStatus.valueOf(rs.getString("user_status"))

                    ));
            rUser.setRoles(findUserRoles(rUser));
            return rUser;
        } catch (EmptyResultDataAccessException e) {
            //throw new ResourceNotFoundException("Privilege not found with this name : " + name);
            return null;
        }
    }

    public List<Role> findUserRoles(User user) {
        List<UsersRolesResponse> usersRolesResponses = new ArrayList<>();
        List<Role> roles = new ArrayList<>();

        String findUserRolesByUserIdSql = "SELECT * FROM users_roles WHERE user_id = ?";

        try {

            usersRolesResponses = jdbcTemplate.query(
                    findUserRolesByUserIdSql, new Object[]{user.getId()},
                    (rs, rowNum) ->
                            new UsersRolesResponse(
                                    rs.getLong("user_id"),
                                    rs.getLong("role_id")
                            ));

            for (UsersRolesResponse u : usersRolesResponses ) {
                roles.add(roleDao.findById(u.getRole_id()));
            }


        }
        catch (EmptyResultDataAccessException e) {
            //throw new ResourceNotFoundException("Privilege not found with this name : " + name);
            return null;
        }
        catch (Exception e) {
            throw new RuntimeException("findUserRoles sql exception : " + e.getMessage());

        }

        return roles;
    }

    public void addRoleToUser(User user,Role role) {
        String addRoleToUserSql = "INSERT INTO users_roles (user_id,role_id) VALUES (?,?);";

        try {
            jdbcTemplate.update(addRoleToUserSql,user.getId(),role.getId());
        }catch (Exception e) {
            throw new RuntimeException("addRoleToUser save sql exception : " + e.getMessage());
        }
    }

    public User save(User user) {

        OffsetDateTime offsetCurrDateTime = OffsetDateTime.now(zoneOffSet);

        String userSaveSql = "INSERT INTO users (password,email,name,login_type,user_status,created_on,last_login)" +
                "VALUES " +
                "(?,?,?,?,?,?,?);";



        try {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(userSaveSql, new String[] {"id"});
                    statement.setString(1,user.getPassword());
                    statement.setString(2,user.getEmail());
                    statement.setString(3,user.getName());
                    statement.setString(4,user.getLoginType());
                    statement.setObject(5, user.getStatus(),Types.OTHER);
                    statement.setObject(6,offsetCurrDateTime);
                    statement.setObject(7,offsetCurrDateTime);
                    return statement;
                }
            }, holder);

            long primaryKey = holder.getKey().longValue();
            user.setId(primaryKey);


            //TODO set user roles batch insert later
            for (Role r  : user.getRoles()) {
                Role rWithId = roleDao.findByName(r.getName());
                addRoleToUser(user,rWithId);
            }

        }  catch (Exception e) {
            throw new RuntimeException("User save sql exception : " + e.getMessage());
        }
        return user;
    }

}
