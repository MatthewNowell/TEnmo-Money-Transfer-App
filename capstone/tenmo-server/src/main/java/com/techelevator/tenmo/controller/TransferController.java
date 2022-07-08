package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.InvalidTransferException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.services.TransferServices;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao transferDao;
    private AccountDao accountDao;
    private TransferServices transferServices = new TransferServices();

    public TransferController(@Qualifier("ActiveTransferDao") TransferDao transferDao, @Qualifier("ActiveAccountDao")AccountDao accountDao){
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        transferServices.setAccountDao(accountDao);
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/{accountId}/account", method = RequestMethod.GET)
    public String getUserNameFromAccountId(@PathVariable int accountId){
        return accountDao.getUserNameByAccountId(accountId);
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/{id}/transfer", method = RequestMethod.GET)
    public Transfer getTransfer(@PathVariable int id){
        return transferDao.getTransfer(id);
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/{userId}/user/balance", method = RequestMethod.GET)
    public List<Account> getListAccounts(@PathVariable int userId){
        return accountDao.getAccountsByUserID(userId);
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/{userId}/user/history", method = RequestMethod.GET)
    public List<Transfer> getAllUserTransfers(@PathVariable int userId){
        List<Transfer> transfers = new ArrayList<>();
        List<Account> accounts = accountDao.getAccountsByUserID(userId);
        for(Account account : accounts){
            transfers.addAll(transferDao.getTransferByAccountNumber(account.getAccountId()));
        }
        return transfers;
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public Transfer postTransfer(@RequestBody @Valid Transfer transfer) throws InvalidTransferException, SQLException {
        if(transfer.getTransferType().equals("Send")) {
            transfer = transferServices.processTransfer(transfer);
        }
        transferDao.addTransfer(transfer);
        transferDao.commitToDatabase();
        return transfer;
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/{requestStatus}/transfer", method = RequestMethod.GET)
    public List<Transfer> getTransactionByStatusCode(@PathVariable String requestStatus){
        return transferDao.getTransfersByTransferStatus(requestStatus);
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/{userId}/user/{requestStatus}/transfer", method = RequestMethod.GET)
    public List<Transfer> getTransfersByStatusCodeAndUser(@PathVariable int userId, @PathVariable String requestStatus){
        return transferDao.getTransfersByUserIdAndTransferStatus(userId, requestStatus);
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(path = "/accounts", method = RequestMethod.GET)
    public List<Account> getAllAccounts(){
        return accountDao.getAccounts();
    }

}
