package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.InvalidTransferException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
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
    public Transfer postTransfer(@RequestBody @Valid Transfer transfer) throws InvalidTransferException{
        Account fromAccount = accountDao.getIndividualAccount(transfer.getAccountFromId());
        Account toAccount = accountDao.getIndividualAccount(transfer.getAccountToId());
        if(fromAccount.getBalance().compareTo(transfer.getAmountToTransfer()) >= 0){
            fromAccount.setBalance(fromAccount.getBalance().subtract(transfer.getAmountToTransfer()));
            accountDao.updateAccount(fromAccount);

            toAccount.setBalance(toAccount.getBalance().add(transfer.getAmountToTransfer()));
            accountDao.updateAccount(toAccount);

            transferDao.addTransfer(transfer);
        } else{
            throw new InvalidTransferException();
        }
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

}
