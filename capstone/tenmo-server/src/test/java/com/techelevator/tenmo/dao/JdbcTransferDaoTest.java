package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tests.BaseDaoTest;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JdbcTransferDaoTest extends BaseDaoTest {

    private JdbcTransferDao sut;

    private static final Transfer TRANSFER_1 = new Transfer(3001,"Send","Approved",2001,2003, BigDecimal.valueOf(500.0));
    private static final Transfer TRANSFER_2 = new Transfer(3002, "Send", "Rejected", 2002, 2004, BigDecimal.valueOf(300.0));

    private static List<Transfer> expected = new ArrayList<>(Arrays.asList(TRANSFER_1, TRANSFER_2));

    @Before
    public void setUp() throws Exception {
        sut = new JdbcTransferDao(dataSource);
    }

    @Test
    public void testGetTransfers() {
        List<Transfer> actual = sut.getTransfers();
        assertTransferMatch(expected, actual);
    }

    @Test
    public void testGetTransfersByTransferType() {
        List<Transfer> actual = sut.getTransfersByTransferType("Send");
        assertTransferMatch(expected, actual);
    }

    @Test
    public void testGetTransfersByTransferStatus() {
        Transfer actual1 = sut.getTransfersByTransferStatus("Approved").get(0);
        Transfer actual2 = sut.getTransfersByTransferStatus("Rejected").get(0);

        assertTransferMatch(TRANSFER_1, actual1);
        assertTransferMatch(TRANSFER_2, actual2);
    }
    
    @Test
    public void testGetTransfersByOutgoingAccount() {
        Transfer actual1 = sut.getTransfersByOutgoingAccount(3001).get(0);
        Transfer actual2 = sut.getTransfersByOutgoingAccount(3002).get(0);

        assertTransferMatch(TRANSFER_1, actual1);
        assertTransferMatch(TRANSFER_2, actual2);

    }

    @Test
    public void testGetTransfersByIncomingAccount() {
        Transfer actual1 = sut.getTransfersByIncomingAccount(3001).get(0);
        Transfer actual2 = sut.getTransfersByIncomingAccount(3002).get(0);

        assertTransferMatch(TRANSFER_1, actual1);
        assertTransferMatch(TRANSFER_2, actual2);

    }

    public void testGetTransferByAccountNumber() {
    }

    public void testGetTransfer() {
    }

    public void testAddTransfer() {
    }

    public void testUpdateTransfer() {
    }

    public void testDeleteTransfer() {
    }

    private void assertTransferMatch(List<Transfer> expectedList, List<Transfer> actualList){
        for(int i = 0; i < actualList.size(); i++){
            assertTransferMatch(expectedList.get(i), actualList.get(i));
        }
    }

    private void assertTransferMatch(Transfer expected, Transfer actual){
        assertEquals("Id doesn't match", expected.getId(), actual.getId());
        assertEquals("Transfer Types doesn't match", expected.getTransferType(), actual.getTransferType());
        assertEquals("Transfer Status doesn't match", expected.getTransferStatus(), actual.getTransferStatus());
        assertEquals("Account From Id doesn't match",expected.getAccountFromId(), actual.getAccountFromId());
        assertEquals("Account To Id doesn't match", expected.getAccountToId(), actual.getAccountToId());
        assertEquals("Amount to transfer doesn't match", expected.getAmountToTransfer().compareTo(actual.getAmountToTransfer()), 0);
    }
}