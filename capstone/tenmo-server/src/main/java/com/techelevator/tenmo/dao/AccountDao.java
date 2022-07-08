package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.sql.SQLException;
import java.util.List;

public interface AccountDao {

    List<Account> getAccounts();
    List<Account> getAccountsByUserID(int userId);
    Account getIndividualAccount(int accountId);
    Account createAccount(Account accountToAdd);
    String getUserNameByAccountId(int accountId);
    void updateAccount(Account accountToUpdate);
    void deleteAccount(int accountId);

    void commitToDatabase() throws SQLException;
}

