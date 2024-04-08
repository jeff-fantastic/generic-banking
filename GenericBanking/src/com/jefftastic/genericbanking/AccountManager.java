package com.jefftastic.genericbanking;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 *  Manages a list of accounts, in ways such as:<br>
 *  - Opening new bank accounts<br>
 *  - Closing existing bank accounts<br>
 *  - Searching through the list of accounts by name<br>
 * @see Account
 */

public class AccountManager {
    // Declare constants
    private final String[] manMesg = {
            """
            
            Hello, to get started, please enter an option from below. (1-2)
            [1] - Search and login
            [2] - Open account
            [0] - Close program
            
            >""",
            """
            
            Hello %s! Please enter an option from below. (1-5)
            [1] - Deposit funds
            [2] - Withdraw funds
            [3] - Account information
            [4] - Close this account
            [5] - Log-out
            [0] - Close program
            
            >""",
    };

    /**
     * Set of constants that determine how the program will update.
     */
    private enum AccessMode {
        /**
         * Provides a generic login prompt that forks
         * into different access modes.
         */
        LOGIN_IDLE,
        /**
         * Searches for a full name provided by end user, and
         * provides a list of exact matches to choose from.
         */
        LOGIN_SEARCH,
        /**
         * Puts user through account open process, then logs
         * them into their new account
         */
        LOGIN_OPEN_ACCOUNT,
        /**
         * Provides a generic account prompt
         */
        ACCOUNT_IDLE,
        /**
         * Puts user through fund deposit process.
         */
        ACCOUNT_DEPOSIT,
        /**
         * Puts user through fund withdraw process.
         */
        ACCOUNT_WITHDRAW,
        /**
         * Prints information about the current account
         * to the console.
         */
        ACCOUNT_INFO,
        /**
         * Closes the current account, removing it from
         * the existing list of accounts
         */
        ACCOUNT_CLOSE,
        /**
         * Closes the program.
         */
        EXIT_PROGRAM

    };

    // Declare class variables
    private List<Account> accounts = new ArrayList<>();
    private Scanner input = new Scanner(System.in);
    private AccessMode mode = AccessMode.LOGIN_IDLE;

    /**
     * Main update loop.
     */
    public int update() {
        switch (mode) {
            case LOGIN_IDLE:            loginIdleLoop();                break;
            case LOGIN_SEARCH:          loginSearchLoop();              break;
            case LOGIN_OPEN_ACCOUNT:    loginOpenAccountLoop();         break;
            case ACCOUNT_IDLE:          accountIdleLoop();              break;
            case ACCOUNT_DEPOSIT:       accountDepositLoop();           break;
            case ACCOUNT_WITHDRAW:      accountWithdrawLoop();          break;
            case ACCOUNT_INFO:          accountInfoLoop();              break;
            case ACCOUNT_CLOSE:         accountCloseLoop();             break;
            case EXIT_PROGRAM:          return -1;
        }
        return 0;
    }

    private void loginIdleLoop() {
        // Declare variables
        int next;

        // Print generic prompt and wait for response
        next = promptUserInt(manMesg[0], input);
        switch (next) {
            case 1:     mode = AccessMode.LOGIN_SEARCH;                 break;
            case 2:     mode = AccessMode.LOGIN_OPEN_ACCOUNT;           break;
            default:
                System.out.println("\nInvalid input, please try again.");
                break;
        }
    }

    private void loginSearchLoop() {
        // Declare variables
        String next;
        List<Account> foundAccounts;

        // Prompt user for name and get accounts
        next = promptUserLine("\nPlease input your full name here.\n", input);
        foundAccounts = findAccount(next);

        // Now print accounts in a list and ask user to pick one
        System.out.println("Heres the list of accounts that we found:\n");
        for (int i = 0; i < foundAccounts.size(); i++) {
            Account acc = foundAccounts.get(i);
            System.out.printf(
                    "[%d] %-30s %30s\n",
                    i,
                    acc.getName(),
                    acc.getAddress()
            );
        }
    }

    private void loginOpenAccountLoop() {
    }

    private void accountIdleLoop() {
    }

    private void accountDepositLoop() {
    }

    private void accountWithdrawLoop() {
    }

    private void accountInfoLoop() {
    }

    private void accountCloseLoop() {
    }


    /**
     * Guides the user through the entire process of opening an account
     * and confirming the information within. If the information is
     * rejected by the end user, restart the account opening process.
     * Otherwise, add the account to the list of existing accounts.
     * @see Account
     */
    public Account openAndConfirmAccount() {
        // Declare variables
        Account newAccount = null;

        while (true) {
            // Run user through opening process
            newAccount = openAccount();

            // Now prompt user about their details
            System.out.printf(
                    """
                    Name provided : %s
                    Address provided : %s
                    Balance deposited : $%.2f
                    
                    Is this information correct? (Y/N)
                    """,
                    newAccount.getName(),
                    newAccount.getAddress(),
                    newAccount.getBalance()
            );
            String response = input.nextLine();

            // Depending on response, break loop
            if (response.toLowerCase(Locale.ROOT).equals("y")) {
                return newAccount;
            }
        }
    }

    /**
     * Guides the user through the account opening process, with loops preventing
     * the input of invalid information.
     * @return The user's opened account
     * @see Account
     */
    private Account openAccount() {
        // Declare variables
        String inputName = null, inputAddress = null;
        double inputBalance = Double.NaN;
        Account newAccount;

        // Ask user for information
        while (inputName == null)
            inputName = promptUserLine("\nWhat is your first and last name?\n", input);

        while (inputAddress == null)
            inputAddress = promptUserLine("\nWhat is your address?\n", input);

        while (Double.isNaN(inputBalance))
            inputBalance = promptUserDouble("\nHow many funds would you like to put in for your initial deposit?\n$", input);

        newAccount = new Account(inputName, inputAddress, inputBalance);
        return newAccount;
    }

    /**
     * Prompts user with provided text and returns the input value as a String.
     * @param prompt Prompt to print in console
     * @param in Scanner to pull input from
     * @return User's input as String
     */
    private String promptUserLine(String prompt, Scanner in) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine() : null;
    }

    /**
     * Prompts user with provided text and returns the input value as a double.
     * @param prompt Prompt to print in console
     * @param in Scanner to pull input from
     * @return User's input as double
     */
    private double promptUserDouble(String prompt, Scanner in) {
        System.out.print(prompt);
        return in.hasNextDouble() ? in.nextDouble() : Double.NaN;
    }

    /**
     * Prompts user with provided text and returns the input value as a integer.
     * @param prompt Prompt to print in console
     * @param in Scanner to pull input from
     * @return User's input as integer
     */
    private int promptUserInt(String prompt, Scanner in) {
        System.out.print(prompt);
        return in.hasNextInt() ? in.nextInt() : Integer.MIN_VALUE;
    }

    /**
     * Provides a list of accounts in this manager's
     * list based on the provided name
     * @param name The name that is being searched for.
     * @return The found account
     */
    private List<Account> findAccount(String name) {
        // Declare variables
        List<Account> foundAccounts = new ArrayList<>();

        // Iterate through accounts and create a list of matches
        for (Account acc : accounts) {
            if (acc.getName().equalsIgnoreCase(name))
                foundAccounts.add(acc);
        }

        return foundAccounts;
    }
}
