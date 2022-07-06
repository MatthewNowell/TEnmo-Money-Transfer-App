package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    List<Transfer> getTransfers();
    List<Transfer> getTransfersByTransferType(String transferType);
    List<Transfer> getTransfersByTransferStatus(String transferStatus);
    List<Transfer> getTransfersByOutgoingAccount(int accountId);
    List<Transfer> getTransfersByIncomingAccount(int accountId);
    List<Transfer> getTransferByAccountNumber(int accountId);
    Transfer getTransfer(int id);

    Transfer addTransfer(Transfer transfer);
    void updateTransfer(Transfer transfer);
    void deleteTransfer(int id);

}