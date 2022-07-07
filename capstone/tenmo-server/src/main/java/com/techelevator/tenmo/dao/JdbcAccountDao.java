package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component("ActiveAccountDao")
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(DataSource dataSource){jdbcTemplate = new JdbcTemplate(dataSource);}


    @Override
    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, user_id, balance "+
                "FROM account "+
                "ORDER BY account_id ASC;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while(sqlRowSet.next()){
            accounts.add(mapRowToAccount(sqlRowSet));
        }
        return accounts;
    }

    @Override
    public List<Account> getAccountsByUserID(int userId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, user_id, balance "+
                "FROM account "+
                "WHERE user_id = ? "+
                "ORDER BY account_id ASC;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, userId);
        while(sqlRowSet.next()){
            accounts.add(mapRowToAccount(sqlRowSet));
        }
        return accounts;
    }

    @Override
    public Account getIndividualAccount(int accountId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance "+
                "FROM account "+
                "WHERE account_id = ?;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, accountId);
        if(sqlRowSet.next()){
            account = mapRowToAccount(sqlRowSet);
        }
        return account;
    }

    @Override
    public Account createAccount(Account accountToAdd) {
        String sql = "INSERT INTO account(user_id, balance) "+
                "VALUES (?,?) "+
                "RETURNING account_id;";
        Integer accountId = jdbcTemplate.queryForObject(sql, Integer.class, accountToAdd.getUserId(), accountToAdd.getBalance());
        return getIndividualAccount(accountId);
    }

    @Override
    public void updateAccount(Account accountToUpdate) {
        String sql = "UPDATE account "+
                "SET user_id = ?, balance = ? "+
                "WHERE account_id = ?;";
        jdbcTemplate.update(sql, accountToUpdate.getUserId(), accountToUpdate.getBalance(), accountToUpdate.getAccountId());
    }

    @Override
    public void deleteAccount(int accountId) {
        String sql = "DELETE FROM transfer WHERE account_to = ? OR account_from = ?; DELETE FROM account WHERE account_id = ?;";
        jdbcTemplate.update(sql, accountId, accountId, accountId);
    }

    private Account mapRowToAccount(SqlRowSet sqlRowSet){
        Account account = new Account();
        account.setAccountId(sqlRowSet.getInt("account_id"));
        account.setUserId(sqlRowSet.getInt("user_id"));
        account.setBalance(sqlRowSet.getBigDecimal("balance"));
        return account;
    }
}
