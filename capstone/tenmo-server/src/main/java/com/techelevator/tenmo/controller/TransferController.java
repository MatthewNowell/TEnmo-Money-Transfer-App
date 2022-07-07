package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController {

    private TransferDao transferDao;
    private AccountDao accountDao;

    public TransferController(@Qualifier("ActiveTransferDao") TransferDao transferDao, @Qualifier("ActiveAccountDao")AccountDao accountDao){
        this.transferDao = transferDao;
        this.accountDao = accountDao;
    }

    

    /* TODO
    Send Transfer of specific Amount
    See user account balance
    See history of all transactions
    See any transfer based on transfer id
     */
}
