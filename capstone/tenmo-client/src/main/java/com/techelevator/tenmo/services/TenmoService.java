package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TenmoService {

    private String basicAPIUrl;
    private int userId;
    private String authToken;
    private final RestTemplate restTemplate = new RestTemplate();

    public TenmoService(String basicAPIUrl){
        this.basicAPIUrl = basicAPIUrl;
    }

    public int getUserId(){return userId;}
    public void setUserId(int userId){this.userId = userId;}

    public void setAuthToken(String authToken){this.authToken = authToken;}

    public Account[] viewCurrentBalance(){
        Account[] currentBalance = null;
        try {
            ResponseEntity<Account[]> response = restTemplate.exchange(basicAPIUrl + "/" + userId + "/user/balance", HttpMethod.GET, makeAuthEntity(), Account[].class);
            currentBalance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return currentBalance;
    }

    public Transfer[] viewTransferHistory(){
        Transfer[] transferHistory = null;
        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(basicAPIUrl + "/" + userId + "/user/history", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transferHistory = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return transferHistory;
    }

    public Transfer[] viewPendingRequests(){
        Transfer[] pendingRequests = null;
        try{
            ResponseEntity<Transfer[]> response = restTemplate.exchange(basicAPIUrl + "/" + userId + "/user/Pending/transfer", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            pendingRequests = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return pendingRequests;
    }

    public Transfer makeTransfer(BigDecimal amountToSend, String transferType, int accountToSendFrom, int accountToSendTo){
        Transfer transferInfo = new Transfer();
        transferInfo.setAmountToTransfer(amountToSend);
        transferInfo.setAccountFromId(accountToSendFrom);
        transferInfo.setAccountToId(accountToSendTo);
        transferInfo.setTransferType(transferType);
        transferInfo.setTransferStatus("Pending");
        HttpEntity<Transfer> entity = makeTransferEntity(transferInfo);

        Transfer returnedTransfer = null;
        try {
            returnedTransfer = restTemplate.postForObject(basicAPIUrl + "/transfer/" + transferType.toLowerCase(), entity, Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return returnedTransfer;
    }

    public boolean sendBucks(int transferId){
        Transfer requestedTransfer = getTransfer(transferId);
        if(requestedTransfer != null){
            requestedTransfer.setTransferStatus("Accepted");
            try {
                restTemplate.put(basicAPIUrl + "/transfer/" + transferId + "/pay", requestedTransfer);
                return true;
            } catch (RestClientResponseException | ResourceAccessException e){
                BasicLogger.log(e.getMessage());
            }
        }
        return false;
    }

    public Transfer getTransfer(int transferId){
        Transfer transfer = null;
        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(basicAPIUrl + "/" + transferId + "/transfer",HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    private HttpEntity<Void> makeAuthEntity(){
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(authToken);
        return new HttpEntity<>(header);
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer){
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(authToken);
        header.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(transfer, header);
    }
}
