package com.ice.simplebanksite.dao;

import com.ice.simplebanksite.authentication.PasswordAuthentication;
import com.ice.simplebanksite.mapper.UserInfoMapper;
import com.ice.simplebanksite.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by Bartek on 2016-12-14.
 */
@Service
@Transactional
public class UserInfoDAOImpl extends JdbcDaoSupport implements UserInfoDAO {

    @Autowired
    public UserInfoDAOImpl(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserInfo findUserInfo(String userName) {
//        String sql = "Select u.Username,u.Password from Users u where u.Username = ? ";
        String sql = "Select u.Username,u.Password from Users u where u.Username = '" + userName + "'";

        Object[] params = new Object[] { userName };
        UserInfoMapper mapper = new UserInfoMapper();
        try {
//            UserInfo userInfo = this.getJdbcTemplate().queryForObject(sql, params, mapper);
            UserInfo userInfo = this.getJdbcTemplate().queryForObject(sql, mapper);

            System.out.println(userInfo.getPassword() + " ---- hash ----" + passwordEncoder.encode(userInfo.getPassword()));
            System.out.println(" ---- hash: System.out.println(userInfo.getPassword() + \" ---- hash ----\" + new PasswordAuthentication().hash(userInfo.getPassword())); ----" + new PasswordAuthentication().authenticate(userInfo.getPassword().toCharArray(),"$31$16$7qE89abML867DKD-qhD02cPxJDPZtqQATWbTq2Lm75w"));
            return userInfo;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Override
    public List<String> getUserRoles(String userName) {
        String sql = "Select r.Role "//
                + " from Roles r inner join Users u on u.id = r.user_id where u.Username = ? ";

        Object[] params = new Object[] { userName };

        List<String> roles = this.getJdbcTemplate().queryForList(sql,params, String.class);

        return roles;
    }

    @Override
    public String getUserName(int id) {
        String sql = "Select u.Username,u.Password "//
                + " from Users u where u.Username = ? ";

        Object[] params = new Object[] { id };
        UserInfoMapper mapper = new UserInfoMapper();
        try {
            UserInfo userInfo = this.getJdbcTemplate().queryForObject(sql, params, mapper);
            return userInfo.getUserName();
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public int getUserId(String userName) {
        String sql = "Select u.id"//
                + " from Users u where u.Username = ? ";

        Object[] params = new Object[] { userName };
        int count = getJdbcTemplate().queryForObject(sql, params, Integer.class);
        return count;
    }

}