package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tests.BaseDaoTest;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JdbcAccountDaoTest extends BaseDaoTest {

    private JdbcAccountDao sut;

    private static final Account ACCOUNT_1 = new Account (2001, 1001, BigDecimal.valueOf(1000));
    private static final Account ACCOUNT_2 = new Account (2002, 1001, BigDecimal.valueOf(10));
    private static final Account ACCOUNT_3 = new Account (2003, 1002, BigDecimal.valueOf(2000));
    private static final Account ACCOUNT_4 = new Account(2004, 1002, BigDecimal.valueOf(20));

    private static List<Account> expected = new ArrayList<>(Arrays.asList(ACCOUNT_1, ACCOUNT_2, ACCOUNT_3,ACCOUNT_4));

    private static Account accountToAdd = new Account (1001, BigDecimal.valueOf(1));
    private static Account accountToUpdate = new Account(2001, 1002, BigDecimal.valueOf(5000000));

    @Before
    public void setUp() throws Exception{
        sut = new JdbcAccountDao(dataSource);
    }

    @Test
    public void testGetAccounts() {
        assertAccountsMatch(expected, sut.getAccounts());
    }

    @Test
    public void testGetAccountsByUserID() {
        assertAccountsMatch(new ArrayList<>(Arrays.asList(ACCOUNT_1, ACCOUNT_2)), sut.getAccountsByUserID(2001));
        assertAccountsMatch(new ArrayList<>(Arrays.asList(ACCOUNT_3, ACCOUNT_4)), sut.getAccountsByUserID(1002));
    }

    @Test
    public void testGetIndividualAccount() {
        assertAccountsMatch(ACCOUNT_1, sut.getIndividualAccount(2001));
        assertAccountsMatch(ACCOUNT_2, sut.getIndividualAccount(2002));
    }

    @Test
    public void testCreateAccount() {
        accountToAdd = sut.createAccount(accountToAdd);
        assertAccountsMatch(accountToAdd, sut.getIndividualAccount(2005));
    }

    @Test
    public void testUpdateAccount() {
        sut.updateAccount(accountToUpdate);
        assertAccountsMatch(accountToUpdate, sut.getIndividualAccount(2001));
    }

    @Test
    public void testDeleteAccount() {
        sut.deleteAccount(2001);
        assertNull(sut.getIndividualAccount(2001));
    }

    @Test
    public void testGetUserNameByAccountId(){
        assertEquals(sut.getUserNameByAccountId(2001), "Sam M");
        assertEquals(sut.getUserNameByAccountId(2003), "Matt N");
    }

    private void assertAccountsMatch(List<Account> expectedList, List<Account> actualList){
        for(int i = 0; i < actualList.size(); i++){
            assertAccountsMatch(expectedList.get(i), actualList.get(i));
        }
    }

    private void assertAccountsMatch(Account expected,Account actual){
        assertEquals("Id doesn't match", expected.getAccountId(), actual.getAccountId());
        assertEquals("UserId doesn't match", expected.getUserId(), actual.getUserId());
        assertEquals("Value doesn't match", expected.getBalance().compareTo(actual.getBalance()), 0);
    }
}