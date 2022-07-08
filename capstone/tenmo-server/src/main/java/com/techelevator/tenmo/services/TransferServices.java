package com.techelevator.tenmo.services;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.exception.InvalidTransferException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

public class TransferServices {

    private static final String REJECTED_STATUS = "Rejected";
    private static final String APPROVED_STATUS = "Approved";
    private static final String PENDING_STATUS = "Pending";

    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Transactional
    public Transfer processTransfer(Transfer incomingTransfer) throws SQLException, InvalidTransferException {
        Account toAccount = accountDao.getIndividualAccount(incomingTransfer.getAccountToId());
        Account fromAccount = accountDao.getIndividualAccount(incomingTransfer.getAccountFromId());
        if(incomingTransfer.getTransferStatus().equalsIgnoreCase(PENDING_STATUS)) {
            if (fromAccount.getBalance().compareTo(incomingTransfer.getAmountToTransfer()) < 0) {
                incomingTransfer.setTransferStatus(REJECTED_STATUS);
            } else {
                incomingTransfer.setTransferStatus(APPROVED_STATUS);
                toAccount.setBalance(toAccount.getBalance().add(incomingTransfer.getAmountToTransfer()));
                fromAccount.setBalance(fromAccount.getBalance().subtract(incomingTransfer.getAmountToTransfer()));
                accountDao.updateAccount(toAccount);
                accountDao.updateAccount(fromAccount);
            }
        }
        else{
            throw new InvalidTransferException();
        }
        return incomingTransfer;
    }

    @Transactional
    public Transfer updateTransfer(Transfer updatedTransfer) throws InvalidTransferException {
        Account toAccount = accountDao.getIndividualAccount(updatedTransfer.getAccountToId());
        Account fromAccount = accountDao.getIndividualAccount(updatedTransfer.getAccountFromId());
        if(updatedTransfer.getTransferStatus().equalsIgnoreCase(APPROVED_STATUS)){
            if (fromAccount.getBalance().compareTo(updatedTransfer.getAmountToTransfer()) < 0) {
                throw new InvalidTransferException();
            }
            toAccount.setBalance(toAccount.getBalance().add(updatedTransfer.getAmountToTransfer()));
            fromAccount.setBalance(fromAccount.getBalance().subtract(updatedTransfer.getAmountToTransfer()));
            accountDao.updateAccount(toAccount);
            accountDao.updateAccount(fromAccount);
            return updatedTransfer;
        } else if(!updatedTransfer.getTransferStatus().equalsIgnoreCase(REJECTED_STATUS)){
            return updatedTransfer;
        } else{
            throw new InvalidTransferException();
        }
    }
}
