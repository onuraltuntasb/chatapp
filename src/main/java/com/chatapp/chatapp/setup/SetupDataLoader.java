package com.chatapp.chatapp.setup;

import com.chatapp.chatapp.dao.PrivilegeDao;
import com.chatapp.chatapp.dao.RoleDao;
import com.chatapp.chatapp.entity.Privilege;
import com.chatapp.chatapp.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    private final JdbcTemplate jdbcTemplate;
    private final PrivilegeDao privilegeDao;
    private final RoleDao roleDao;

    //TODO move scripts to db to run script once for local and prod databases (avoid data override)

    boolean alreadySetup = true;

    // add dates test add status test


    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;

        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE;");
        jdbcTemplate.execute("DROP TYPE IF EXISTS UserStatus CASCADE;");
        jdbcTemplate.execute("CREATE TYPE UserStatus AS ENUM( 'ACTIVE', 'CLOSED', 'CANCELED', 'BLACKLISTED', 'NONE');");
        jdbcTemplate.execute("CREATE TABLE users (\n" +
                                     "\tid serial PRIMARY KEY,\n" +
                                     "\tpassword VARCHAR ( 500 ) NOT NULL,\n" +
                                     "\temail VARCHAR ( 255 ) UNIQUE NOT NULL,\n" +
                                     "\tname VARCHAR ( 255 )  NOT NULL,\n" +
                                     "\tlogin_type VARCHAR ( 255 )  NOT NULL,\n" +
                                     "\tuser_status UserStatus DEFAULT 'NONE',\n" +
                                     "\tcreated_on TIMESTAMP NOT NULL,\n" +
                                     "        last_login TIMESTAMP \n" +
                                     ");");

        jdbcTemplate.execute("DROP TABLE IF EXISTS privilege CASCADE;");
        jdbcTemplate.execute("CREATE TABLE privilege (id serial PRIMARY KEY, name VARCHAR ( 255 ) UNIQUE NOT NULL) ;");

        jdbcTemplate.execute("DROP TABLE IF EXISTS role CASCADE; ");
        jdbcTemplate.execute("CREATE TABLE role (id serial PRIMARY KEY, name VARCHAR ( 255 ) UNIQUE NOT NULL) ;");


        jdbcTemplate.execute("DROP TABLE IF EXISTS roles_privileges CASCADE;");
        jdbcTemplate.execute("CREATE TABLE roles_privileges (\n" +
                                     "  role_id INT NOT NULL,\n" +
                                     "  privilege_id INT NOT NULL,\n" +
                                     "  PRIMARY KEY (role_id, privilege_id),\n" +
                                     "  CONSTRAINT fk_role FOREIGN KEY(role_id) REFERENCES role(id),\n" +
                                     "  CONSTRAINT fk_privilege FOREIGN KEY(privilege_id) REFERENCES privilege(id)\n" +
                                     ");");

        jdbcTemplate.execute("DROP TABLE IF EXISTS users_roles CASCADE;");
        jdbcTemplate.execute("CREATE TABLE users_roles (\n" +
                                     "  user_id INT NOT NULL,\n" +
                                     "  role_id INT NOT NULL,\n" +
                                     "  PRIMARY KEY (user_id, role_id),\n" +
                                     "  CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id),\n" +
                                     "  CONSTRAINT fk_role FOREIGN KEY(role_id) REFERENCES role(id)\n" +
                                     ");");

        jdbcTemplate.execute("DROP TABLE IF EXISTS refresh_token CASCADE;");
        jdbcTemplate.execute("CREATE TABLE refresh_token " +
                                     "(id serial PRIMARY KEY," +
                                     " expiry_date TIMESTAMP NOT NULL," +
                                     " token VARCHAR (255) NOT NULL," +
                                     "user_id INT NOT NULL UNIQUE REFERENCES users(id) ) ;");

        jdbcTemplate.execute("DROP TABLE IF EXISTS password_reset_token CASCADE;");
        jdbcTemplate.execute("CREATE TABLE password_reset_token " +
                                     "(id serial PRIMARY KEY," +
                                     " expiry_date TIMESTAMP NOT NULL," +
                                     " token VARCHAR (255) NOT NULL," +
                                     "user_id INT NOT NULL UNIQUE REFERENCES users(id) ) ;");

        jdbcTemplate.execute("DROP TABLE IF EXISTS room CASCADE;");
        jdbcTemplate.execute("CREATE TABLE room (id serial PRIMARY KEY, name VARCHAR ( 255 ), admin INT UNIQUE NOT " +
                                     "NULL)" +
                                     " ;");


        jdbcTemplate.execute("DROP TABLE IF EXISTS message CASCADE;");
        jdbcTemplate.execute("CREATE TABLE message (\n" +
                                     "id serial PRIMARY KEY,\n" +
                                     "  user_id INT NOT NULL,\n" +
                                     "  room_id INT NOT NULL,\n" +
                                     "  content VARCHAR(65535 ) NOT NULL,\n" +
                                     "  sentAt TIMESTAMP NOT NULL," +
                                     "  deliveredAt TIMESTAMP NOT NULL," +
                                     "  seenAt TIMESTAMP NOT NULL," +
                                     "  CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id),\n" +
                                     "  CONSTRAINT fk_room FOREIGN KEY(room_id) REFERENCES room(id)\n" +
                                     ");");


        jdbcTemplate.execute("DROP TABLE IF EXISTS users_rooms CASCADE;");
        jdbcTemplate.execute("CREATE TABLE users_rooms (\n" +
                                     "id serial PRIMARY KEY,\n" +
                                     "  user_id INT NOT NULL,\n" +
                                     "  room_id INT NOT NULL,\n" +
                                     "  CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id),\n" +
                                     "  CONSTRAINT fk_room FOREIGN KEY(room_id) REFERENCES room(id)\n" +
                                     ");");

        jdbcTemplate.execute("DROP TABLE IF EXISTS friend CASCADE;");
        jdbcTemplate.execute("CREATE TABLE friend (\n" +
                                     "id serial PRIMARY KEY,\n" +
                                     "  user_id INT NOT NULL,\n" +
                                     "  friend_id INT UNIQUE NOT NULL,\n" +
                                     "  CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id)\n" +
                                     ");");


        //TODO roles and privileges

        Privilege readPrivilege
                = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));


        alreadySetup = true;

//        try {
//            jdbcTemplate.execute("CREATE DATABASE chat_app;");
//        } catch (DataAccessException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Transactional
    public Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeDao.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeDao.save(privilege);
        }
        return privilege;
    }

    @Transactional
    public Role createRoleIfNotFound(
            String name, List<Privilege> privileges) {

        Role role = roleDao.findByName(name);
        if (role == null) {

            role = roleDao.save(Role.builder().name(name).build());

            //TODO batch insert
            for (Privilege pr : privileges) {
                roleDao.addPrivilegeToRole(role, pr);
            }
        }
        return role;
    }

}
