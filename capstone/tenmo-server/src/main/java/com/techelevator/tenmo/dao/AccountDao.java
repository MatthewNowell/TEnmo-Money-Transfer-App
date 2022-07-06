package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.util.List;

public interface AccountDao {

    List<Account> getAccounts();
    List<Account> getAccountsByUserID(int userId);
    Account individualAccount(int accountId);
    Account createAccount(Account accountToAdd);
    void updateAccount(Account accountToUpdate);
    void deleteAccount(int accountId);

}

