package com.techelevator.tenmo;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;

import java.math.BigDecimal;
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
            System.out.println(String.format("|ID|%-6d|Transfer Type|%7s|Transfer Status|%7s|Account From|%-10s|Account To|%-10s|Amount Of Transfer|%.2f", transfer.getId(), transfer.getTransferType(), transfer.getTransferStatus(), tenmoService.convertAccountIdToUserName(currentUser, transfer.getAccountFromId()), tenmoService.convertAccountIdToUserName(currentUser, transfer.getAccountToId()), transfer.getAmountToTransfer().doubleValue()));
        }
	}

	private void viewPendingRequests() {
        Transfer[] transfers = tenmoService.viewPendingRequests(currentUser);
        for(Transfer transfer : transfers){
            System.out.println(String.format("|ID|%-6d|Transfer Type|%7s|Transfer Status|%7s|Account From|%-10s|Account To|%-10s|Amount Of Transfer|%.2f", transfer.getId(), transfer.getTransferType(), transfer.getTransferStatus(), tenmoService.convertAccountIdToUserName(currentUser, transfer.getAccountFromId()), tenmoService.convertAccountIdToUserName(currentUser, transfer.getAccountToId()), transfer.getAmountToTransfer().doubleValue()));
        }
	}

	private void sendBucks() {
		BigDecimal amountToSend;
        int accountToId = 0;

        try{
            System.out.print("Please enter a valid amount to send to your friend!! > ");
            amountToSend = BigDecimal.valueOf(Double.parseDouble(scanner.nextLine()));
            if(amountToSend.compareTo(BigDecimal.valueOf(0)) <=0 ){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e){
            System.err.println("Please enter a valid amount");
        }

        Account[] accounts = tenmoService.getAllAccounts(currentUser);
        for(Account account : accounts){
            if(account.getUserId() != currentUser.getUser().getId()){
                System.out.println(String.format("|Account Holder|%-10s|Current Balance|$ %2.2f", tenmoService.convertAccountIdToUserName(currentUser, account.getAccountId()), account.getBalance().doubleValue()));
            }
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
