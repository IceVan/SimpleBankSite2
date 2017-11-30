package com.ice.simplebanksite.dao;

import com.ice.simplebanksite.mapper.TransactionMapper;
import com.ice.simplebanksite.mapper.UserInfoMapper;
import com.ice.simplebanksite.model.Balance;
import com.ice.simplebanksite.model.Transactions;
import com.ice.simplebanksite.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Bartek on 2016-12-18.
 */
@Service
@Transactional
public class TransactionDAOImpl extends JdbcDaoSupport implements TransactionDAO {

    @Autowired
    private UserInfoDAO userDao;

    @Autowired
    public TransactionDAOImpl(DataSource dataSource) { this.setDataSource(dataSource);}

    //td
    /*@Override
    public boolean createTransaction(Transactions transaction)
    {
        int rows = getRowsCount();
        String sql = "INSERT INTO transactions (ID, USER_ID, ADDRESS, DESCRIPTION, AMOUNT, TRANSACTION_STATUS) VALUES(?,?,?,?,?,?)";
        Object[] params = new Object[]{ rows+1,
                                        userDao.getUserId(transaction.getUser()),
                                        transaction.getAccountAddress(),
                                        transaction.getDescription(),
                                        transaction.getAmount(),
                                        0};
        int[] types = new int[] { Types.INTEGER,
                                    Types.VARCHAR,
                                    Types.VARCHAR,
                                    Types.VARCHAR,
                                    Types.FLOAT,
                                    Types.SMALLINT};

        rows = this.getJdbcTemplate().update(sql, params, types);
        if(rows == 1)   return  true;
        else            return false;
    }*/

    @Override
    public boolean createTransaction(Transactions transaction)
    {
        int rows = getRowsCount();
        String sql = "INSERT INTO transactions (ID, USER_ID, ADDRESS, DESCRIPTION, AMOUNT, TRANSACTION_STATUS) VALUES("+(rows+1)+",'"+userDao.getUserId(transaction.getUser())+"','"+transaction.getAccountAddress()+"','"+transaction.getDescription()+"',"+transaction.getAmount()+",0)";

        this.getJdbcTemplate().batchUpdate(sql);
        return true;
    }

    @Override
    public Transactions getTransaction(int id)
    {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        return (Transactions)this.getJdbcTemplate().queryForObject(sql, new TransactionMapper(), id);
    }

    @Override //??
    public Balance getBalance(String userName)
    {
        String sql = "SELECT balance FROM users WHERE username = ?";
        System.out.println("username = "+ userName + " /n end of username");
        return new Balance(this.getJdbcTemplate().queryForObject(sql, Double.class, userName));
    }

    @Override //??
    public Balance getBalance(int id)
    {
        String sql = "SELECT balance FROM users WHERE id = ?";
        return new Balance(this.getJdbcTemplate().queryForObject(sql, Double.class, id));
    }

    @Override
    public boolean changeTransactionStatus(String status)
    {
        int id = Integer.parseInt(status.substring(0,status.length()-1));
        Transactions transaction = getTransaction(id);
        int iStatus = (status.charAt(status.length()-1) == 'a' ? 1 : -1);
        String sql = "UPDATE transactions SET transaction_status = ? WHERE id = ?";
        Object[] params = new Object[]{ iStatus,
                                        transaction.getTransID()};
        int rows = this.getJdbcTemplate().update(sql,params);

        Balance userBalance = getBalance(transaction.getUserId());
        sql = "UPDATE users SET balance = ? WHERE username = ?";
        params = new Object[]{ userBalance.changeBalance(-transaction.getAmount()),
                transaction.getUser()};
        rows += this.getJdbcTemplate().update(sql,params);

        userBalance = getBalance(transaction.getAccountAddress());
        sql = "UPDATE users SET balance = ? WHERE username = ?";
        params = new Object[]{ userBalance.changeBalance(transaction.getAmount()),
                transaction.getAccountAddress()};
        rows += this.getJdbcTemplate().update(sql,params);

        if(rows == 3)   return  true;
        else            return false;
    }

    @Override
    public List<Transactions> getTransactionsList(String username)
    {
        String sql = "SELECT u.username, t.address, t.description, t.amount, t.transaction_status, t.id FROM transactions t inner join Users u on u.id = t.user_id  WHERE u.username = ? OR t.address = ?";
//        List<Transactions> transactions = this.getJdbcTemplate().query(sql, new Object[] {username}, new BeanPropertyRowMapper(Transactions.class));

        final List<Transactions> transactions = new ArrayList<Transactions>();
        final List<Map<String, Object>> rows = this.getJdbcTemplate().queryForList(sql, username, username);

        for (Map<String, Object> row : rows) {
            Transactions t = new Transactions((String)row.get("Username"),
                    (String) row.get("Address"),
                    (String) row.get("Description"),
                    (float) row.get("Amount"),
                    (int) row.get("Transaction_Status"),
                    (int) row.get("ID"));
            transactions.add(t);
        }

        return transactions;
    }

    @Override
    public List<Transactions> getAllUsersTransactionsList()
    {
        String sql = "SELECT u.username, t.* FROM transactions t inner join Users u on t.user_id = u.id";
//        List<Transactions> transactions  = getJdbcTemplate().query(sql, new BeanPropertyRowMapper(Transactions.class));

        final List<Transactions> transactions = new ArrayList<Transactions>();
        final List<Map<String, Object>> rows = this.getJdbcTemplate().queryForList(sql);

        for (Map<String, Object> row : rows) {
            Transactions t = new Transactions((String)row.get("Username"),
                    (String) row.get("Address"),
                    (String) row.get("Description"),
                    (float) row.get("Amount"),
                    (int) row.get("Transaction_Status"),
                    (int) row.get("id"));
            transactions.add(t);
        }

        return transactions;
    }

    private int getRowsCount()
    {
        String sql = "SELECT COUNT(*) FROM transactions";
        int count = getJdbcTemplate().queryForObject(sql, Integer.class);
        return count;
    }
}
