package com.techelevator.tenmo.services;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.exception.InvalidTransferException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tests.BaseDaoTest;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class TransferServicesTest extends BaseDaoTest {

    private AccountDao dao;
    private TransferServices sut;

    private static final Transfer TRANSFER_1 = new Transfer("Send", "Pending", 2001, 2002, BigDecimal.valueOf(100));
    private static final Transfer TRANSFER_2 = new Transfer("Send", "Pending", 2002, 2001, BigDecimal.valueOf(100));

    @Before
    public void setUp() throws Exception {
        dao = new JdbcAccountDao(dataSource);
        sut = new TransferServices();
        sut.setAccountDao(dao);
    }

    @Test
    public void testProcessTransfer() throws SQLException, InvalidTransferException {
        assertEquals("TransferStatus does not match", "Rejected", sut.processTransfer(TRANSFER_2).getTransferStatus());
        assertEquals("TransferStatus does not match", "Accepted", sut.processTransfer(TRANSFER_1).getTransferStatus());
    }
}