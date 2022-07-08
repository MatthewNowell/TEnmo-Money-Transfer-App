package com.techelevator.tenmo.services;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.InvalidTransferException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.sql.SQLException;

public class TransferServices {

    private static final String REJECTED_STATUS = "Rejected";
    private static final String ACCEPTED_STATUS = "Approved";
    private static final String PENDING_STATUS = "Pending";

    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public Transfer processTransfer(Transfer incomingTransfer) throws SQLException, InvalidTransferException {
        Account toAccount = accountDao.getIndividualAccount(incomingTransfer.getAccountToId());
        Account fromAccount = accountDao.getIndividualAccount(incomingTransfer.getAccountFromId());
        if(incomingTransfer.getTransferStatus().equals(PENDING_STATUS)) {
            if (fromAccount.getBalance().compareTo(incomingTransfer.getAmountToTransfer()) < 0) {
                incomingTransfer.setTransferStatus(REJECTED_STATUS);
            } else {
                incomingTransfer.setTransferStatus(ACCEPTED_STATUS);
                toAccount.setBalance(toAccount.getBalance().add(incomingTransfer.getAmountToTransfer()));
                fromAccount.setBalance(fromAccount.getBalance().subtract(incomingTransfer.getAmountToTransfer()));
                accountDao.updateAccount(toAccount);
                accountDao.updateAccount(fromAccount);
                //accountDao.commitToDatabase();
            }
        }
        else{
            throw new InvalidTransferException();
        }
        return incomingTransfer;
    }
}
