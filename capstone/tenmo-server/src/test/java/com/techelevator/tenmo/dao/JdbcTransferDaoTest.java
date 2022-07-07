package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tests.BaseDaoTest;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JdbcTransferDaoTest extends BaseDaoTest {

    private JdbcTransferDao sut;

    @Before
    public void setUp() throws Exception {
        sut = new JdbcTransferDao(dataSource);
    }

    @Test
    public void testGetTransfers() {

    }

    public void testGetTransfersByTransferType() {
    }

    public void testGetTransfersByTransferStatus() {
    }

    public void testGetTransfersByOutgoingAccount() {
    }

    public void testGetTransfersByIncomingAccount() {
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
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTransferType(), actual.getTransferType());
        assertEquals(expected.getTransferStatus(), actual.getTransferStatus());
        assertEquals(expected.getAccountFromId(), actual.getAccountFromId());
        assertEquals(expected.getAccountToId(), actual.getAccountToId());
        assertEquals(expected.getAmountToTransfer(), actual.getAmountToTransfer());
    }
}