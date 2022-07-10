package com.techelevator.tenmo;

import com.techelevator.tenmo.exceptions.InvalidTransferException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final TenmoService tenmoService = new TenmoService(API_BASE_URL);

    private final Scanner scanner = new Scanner(System.in);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 6){
                retrieveTransfer();
            } else if (menuSelection == 7){
                payRequest();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        Account[] accounts = tenmoService.viewCurrentBalance(currentUser);
        for(Account account : accounts){
            System.out.println(String.format("|Current Balance|$ %2.2f", account.getBalance().doubleValue()));
        }
	}

	private void viewTransferHistory() {
		Transfer[] transfers = tenmoService.viewTransferHistory(currentUser);
        for(Transfer transfer : transfers){
            System.out.println(String.format("|ID|%-6d|Transfer Type|%7s|Transfer Status|%8s|Account From|%-10s|Account To|%-10s|Amount Of Transfer|%.2f", transfer.getId(), transfer.getTransferType(), transfer.getTransferStatus(), tenmoService.convertAccountIdToUserName(currentUser, transfer.getAccountFromId()), tenmoService.convertAccountIdToUserName(currentUser, transfer.getAccountToId()), transfer.getAmountToTransfer().doubleValue()));
        }
	}

	private void viewPendingRequests() {
        Transfer[] transfers = tenmoService.viewPendingRequests(currentUser);
        for(Transfer transfer : transfers){
            System.out.println(String.format("|ID|%-6d|Transfer Type|%7s|Transfer Status|%8s|Account From|%-10s|Account To|%-10s|Amount Of Transfer|%.2f", transfer.getId(), transfer.getTransferType(), transfer.getTransferStatus(), tenmoService.convertAccountIdToUserName(currentUser, transfer.getAccountFromId()), tenmoService.convertAccountIdToUserName(currentUser, transfer.getAccountToId()), transfer.getAmountToTransfer().doubleValue()));
        }
	}

	private void sendBucks() {
        BigDecimal amountToSend = getAmountToSend();
        int accountToSend;
        try {
            accountToSend = getAccountToSend(tenmoService.getAllAccounts(currentUser));
        } catch (InvalidTransferException e){
            System.err.println("Invalid Transfer");
            return;
        }
        int accountFromSend = tenmoService.viewCurrentBalance(currentUser)[0].getAccountId();
        Transfer transfer = tenmoService.makeTransfer(currentUser, amountToSend, "Send", accountFromSend, accountToSend);
        System.out.println("Money Sent!");
        System.out.println(transfer.printPretty());
	}

	private void requestBucks() {
        BigDecimal amountToSend = getAmountToSend();
        int accountToSend;
        try {
            accountToSend = getAccountToSend(tenmoService.getAllAccounts(currentUser));
        } catch (InvalidTransferException e){
            System.err.println("Invalid Transfer");
            return;
        }
        int accountFromSend = tenmoService.viewCurrentBalance(currentUser)[0].getAccountId();
        tenmoService.makeTransfer(currentUser,amountToSend, "Request", accountFromSend, accountToSend);
        System.out.println("Money Requested!");
	}

    private void retrieveTransfer(){
        Transfer transfer = null;
        System.out.println("Please enter a valid Transfer ID > ");
        int transferId = 0;
        try{
            transferId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e){
            System.err.println("Invalid entry");
        }
        if(transferId != 0) {
            transfer = tenmoService.getTransfer(currentUser, transferId);
            if(transfer == null){
                System.out.println("That Transfer Id does not exist");
            } else {
                System.out.println(transfer.printPretty());
            }
        }
    }

    private void payRequest() {
        Transfer[] transfers = tenmoService.viewPendingRequests(currentUser);
        Transfer selectedTransfer = null;
        int transferId = 0;
        System.out.println("Here is the list of current Pending Transfers");
        for (Transfer t : transfers) {
            System.out.println(t.printPretty());
        }

        System.out.println("Please enter a valid Transfer ID > ");
        try {
            transferId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Invalid entry");
            return;
        }
        try{
            if (transferId != 0) {
                for (Transfer t : transfers) {
                    if (t.getId() == transferId) {
                        System.out.println("Would you like to (A)ccept or (R)eject this transaction? >");
                        String input = scanner.nextLine();
                        if(input.equalsIgnoreCase("a")){
                            boolean success = tenmoService.sendBucks(currentUser, t.getId(), "Approved");
                            if(success) {
                                System.out.println("Money Sent!");
                            } else {
                                System.err.println("Something went wrong, Please try again later");
                            }
                            return;
                        } else if(input.equalsIgnoreCase("r")){
                            boolean success = tenmoService.sendBucks(currentUser, t.getId(), "Rejected");
                            if(success) {
                                System.out.println("Request Rejected!");
                            } else{
                                System.err.println("Something went wrong, Please try again later");
                            }
                            return;
                        } else{
                            System.err.println("Invalid entry");
                        }
                    }
                }
                throw new InvalidTransferException();

            }
        } catch (InvalidTransferException e){
            System.err.println("Invalid Transfer Id");
            }
    }


    private BigDecimal getAmountToSend(){
        BigDecimal amountToSend = null;

        while (amountToSend == null){
            try {
                System.out.print("Please enter an amount of money to send/request >");
                amountToSend = BigDecimal.valueOf(Double.parseDouble(scanner.nextLine()));
                if (amountToSend.compareTo(BigDecimal.valueOf(0)) <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.err.println("Please enter a valid amount");
                amountToSend = null;
            }
        }
        return amountToSend;
    }

    private int getAccountToSend(Account[] accounts) throws InvalidTransferException{
        int accountToSend = 0;
        boolean accountSet = false;
        Map<String,Integer> accountMap = new HashMap<>();

        for(Account account : accounts){
            if(account.getUserId() != currentUser.getUser().getId()){
                String userName = tenmoService.convertAccountIdToUserName(currentUser, account.getAccountId());
                accountMap.put(userName.toLowerCase(), account.getAccountId());
                System.out.println(String.format("|Account Holder|%-10s|", userName));
            }
        }

        while (!accountSet){
            System.out.print("Please enter the username you would like to send/request >");
            String accountName = scanner.nextLine().toLowerCase();
            if(accountName.equalsIgnoreCase(currentUser.getUser().getUsername())){
                throw new InvalidTransferException();
            }
            accountToSend = accountMap.get(accountName);
            for(Account account : accounts){
                if(account.getAccountId() == accountToSend){
                    accountSet = true;
                }
            }
            if(!accountSet) {
                System.err.println("Please enter a valid user");
            }
        }
        return accountToSend;
    }


}
