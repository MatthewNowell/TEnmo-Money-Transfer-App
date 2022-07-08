package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TenmoService {

    private String basicAPIUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public TenmoService(String basicAPIUrl){
        this.basicAPIUrl = basicAPIUrl;
    }

    public Account[] getAllAccounts(AuthenticatedUser user){
        Account[] accounts = null;
        try {
            ResponseEntity<Account[]> response = restTemplate.exchange(basicAPIUrl + "/account", HttpMethod.GET, makeAuthEntity(user.getToken()), Account[].class);
            accounts = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return accounts;
    }

    public String convertAccountIdToUserName(AuthenticatedUser user, int accountId){
        String userName = null;
        try{
            ResponseEntity<String> response = restTemplate.exchange(basicAPIUrl + "/" + accountId + "/account", HttpMethod.GET, makeAuthEntity(user.getToken()), String.class);
            userName = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return userName;
    }

    public Account[] viewCurrentBalance(AuthenticatedUser user){
        Account[] currentBalance = null;
        try {
            ResponseEntity<Account[]> response = restTemplate.exchange(basicAPIUrl + "/" + user.getUser().getId() + "/user/balance", HttpMethod.GET, makeAuthEntity(user.getToken()), Account[].class);
            currentBalance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return currentBalance;
    }

    public Transfer[] viewTransferHistory(AuthenticatedUser user){
        Transfer[] transferHistory = null;
        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(basicAPIUrl + "/" + user.getUser().getId() + "/user/history", HttpMethod.GET, makeAuthEntity(user.getToken()), Transfer[].class);
            transferHistory = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return transferHistory;
    }

    public Transfer[] viewPendingRequests(AuthenticatedUser user){
        Transfer[] pendingRequests = null;
        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(basicAPIUrl + "/" + user.getUser().getId() + "/user/Pending/transfer", HttpMethod.GET, makeAuthEntity(user.getToken()), Transfer[].class);
            pendingRequests = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return pendingRequests;
    }

    public Transfer makeTransfer(AuthenticatedUser user, BigDecimal amountToSend, String transferType, int accountToSendFrom, int accountToSendTo){
        Transfer transferInfo = new Transfer();
        transferInfo.setAmountToTransfer(amountToSend);
        transferInfo.setAccountFromId(accountToSendFrom);
        transferInfo.setAccountToId(accountToSendTo);
        transferInfo.setTransferType(transferType);
        transferInfo.setTransferStatus("Pending");
        HttpEntity<Transfer> entity = makeTransferEntity(transferInfo, user.getToken());

        Transfer returnedTransfer = null;
        try {
            returnedTransfer = restTemplate.postForObject(basicAPIUrl + "/transfer/", entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return returnedTransfer;
    }

    public boolean sendBucks(AuthenticatedUser user, int transferId){
        Transfer requestedTransfer = getTransfer(user, transferId);
        if(requestedTransfer != null){
            try {
                restTemplate.put(basicAPIUrl + "/transfer/" + transferId + "/pay", requestedTransfer);
                return true;
            } catch (RestClientResponseException | ResourceAccessException e){
                BasicLogger.log(e.getMessage());
            }
        }
        return false;
    }

    public Transfer getTransfer(AuthenticatedUser user, int transferId){
        Transfer transfer = null;
        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(basicAPIUrl + "/" + transferId + "/transfer",HttpMethod.GET, makeAuthEntity(user.getToken()), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    private HttpEntity<Void> makeAuthEntity(String authToken){
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(authToken);
        return new HttpEntity<>(header);
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer, String authToken){
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(authToken);
        header.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(transfer, header);
    }
}
