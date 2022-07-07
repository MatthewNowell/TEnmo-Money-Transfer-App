package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao transferDao;
    private AccountDao accountDao;

    public TransferController(@Qualifier("ActiveTransferDao") TransferDao transferDao, @Qualifier("ActiveAccountDao")AccountDao accountDao){
        this.transferDao = transferDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/{id}/transfer", method = RequestMethod.GET)
    public Transfer getTransfer(@PathVariable int id){
        return transferDao.getTransfer(id);
    }

    @RequestMapping(path = "{userId}/user/balance", method = RequestMethod.GET)
    public List<Account> getListAccounts(@PathVariable int userId){
        return accountDao.getAccountsByUserID(userId);
    }

    @RequestMapping(path = "{userId}/user/history", method = RequestMethod.GET)
    public List<Transfer> getAllUserTransfers(@PathVariable int userId){
        List<Transfer> transfers = new ArrayList<>();
        List<Account> accounts = accountDao.getAccountsByUserID(userId);
        for(Account account : accounts){
            transfers.addAll(transferDao.getTransferByAccountNumber(account.getAccountId()));
        }
        return transfers;
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public Transfer postTransfer(@RequestBody @Valid Transfer transfer){
        Account fromAccount = accountDao.getIndividualAccount(transfer.getAccountFromId());
        Account toAccount = accountDao.getIndividualAccount(transfer.getAccountToId());
        if(fromAccount.getBalance().compareTo(transfer.getAmountToTransfer()) >= 0){
            fromAccount.setBalance(fromAccount.getBalance().subtract(transfer.getAmountToTransfer()));
            accountDao.updateAccount(fromAccount);

            toAccount.setBalance(toAccount.getBalance().add(transfer.getAmountToTransfer()));
            accountDao.updateAccount(toAccount);

            transferDao.updateTransfer(transfer);
        } else{

        }
        return transfer;
    }
}
