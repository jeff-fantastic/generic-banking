package com.jefftastic.genericbanking;

import java.util.*;

/**
 *  Manages a list of accounts, in ways such as:<br>
 *  - Withdrawing/depositing money into accounts<br>
 *  - Opening new bank accounts<br>
 *  - Closing existing bank accounts<br>
 *  - Searching through the list of accounts by name<br>
 * @see Account
 */

public class AccountManager {
    // Declare constants
    private final String[] manMesg = {
            """
            
            Hello, to get started, please enter an option from below. (0-2)
            
            [1] - Search and login
            [2] - Open account
            
            [0] - Close program
            
            >""",
            """
            
            Hello %s! You currently have $%0.2f.
            Please enter an option from below. (0-5)
            
            Fund Management
            [1] - Deposit funds
            [2] - Withdraw funds
            
            Account Settings
            [3] - Account information
            [4] - Log-out
            [5] - Close this account
            
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
         * Logs user out of their account.
         */
        ACCOUNT_LOGOUT,
        /**
         * Closes the current account, removing it from
         * the existing list of accounts
         */
        ACCOUNT_CLOSE,
        /**
         * Closes the program.
         */
        EXIT_PROGRAM
    }

    // Declare class variables
    private List<Account> accounts = new ArrayList<>();
    private Account currentAccount;
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
            case ACCOUNT_LOGOUT:        accountLogoutLoop();            break;
            case ACCOUNT_CLOSE:         accountCloseLoop();             break;
            case EXIT_PROGRAM:          return -1;
        }
        return 0;
    }

    private void loginIdleLoop() {
        // Declare variables
        int next;

        // If we've got a current account, just switch
        // to ACCOUNT_IDLE instead.
        if (currentAccount != null) {
            mode = AccessMode.ACCOUNT_IDLE;
            return;
        }

        // Print generic prompt and wait for response
        next = promptUserInt(manMesg[0], input);
        switch (next) {
            case 0:     mode = AccessMode.EXIT_PROGRAM;                 break;
            case 1:     mode = AccessMode.LOGIN_SEARCH;                 break;
            case 2:     mode = AccessMode.LOGIN_OPEN_ACCOUNT;           break;
            default:
                System.out.println("\nInvalid input, please try again.\n");
                break;
        }
    }

    private void loginSearchLoop() {
        // Declare variables
        String nextStr;
        int nextInt;
        List<Account> foundAccounts;

        // Prompt user for name and get accounts
        nextStr = promptUserLine("\nPlease input your full name here.\n>", input);
        foundAccounts = findAccount(nextStr);

        // Now print accounts in a list and ask user to pick one
        System.out.println("Here is the list of accounts that we found:\n");
        for (int i = 0; i < foundAccounts.size(); i++) {
            Account acc = foundAccounts.get(i);
            System.out.printf(
                    "[%d] - %-30s %30s\n",
                    i + 1,
                    acc.getName(),
                    acc.getAddress()
            );
        }
        nextInt = promptUserInt("[0] - Exit\n\nSelect one from the list to proceed.\n>", input);

        // Go back to LOGIN_IDLE and set current account if applicable
        mode = AccessMode.LOGIN_IDLE;
        if (nextInt != 0 && nextInt <= foundAccounts.size())
            currentAccount = foundAccounts.get(nextInt - 1);
    }

    private void loginOpenAccountLoop() {
        // Declare variables
        Account acc;

        // Open and confirm account
        acc = openAndConfirmAccount();

        // Add this to list, and set as current
        accounts.add(acc);
        currentAccount = acc;
        mode = AccessMode.ACCOUNT_IDLE;
    }

    private void accountIdleLoop() {
        // Declare variables
        int next;

        // If we DON'T have a current account, switch
        // to LOGIN_IDLE instead.
        if (currentAccount == null) {
            mode = AccessMode.LOGIN_IDLE;
            return;
        }

        // Print generic prompt and wait for response
        next = promptUserInt(manMesg[1].formatted(
                currentAccount.getName(), currentAccount.getBalance()), input);
        switch (next) {
            case 0:     mode = AccessMode.EXIT_PROGRAM;                 break;
            case 1:     mode = AccessMode.ACCOUNT_DEPOSIT;              break;
            case 2:     mode = AccessMode.ACCOUNT_WITHDRAW;             break;
            case 3:     mode = AccessMode.ACCOUNT_INFO;                 break;
            case 4:     mode = AccessMode.ACCOUNT_LOGOUT;               break;
            case 5:     mode = AccessMode.ACCOUNT_CLOSE;                break;
            default:
                System.out.println("\nInvalid input, please try again.\n");
                break;
        }
    }

    private void accountDepositLoop() {
        // Ask for deposit amount
        double amount = promptUserDouble("\nHow much would you like to deposit?\n$", input);

        // Deposit amount and continue
        currentAccount.setBalance(currentAccount.getBalance() + amount);
        mode = AccessMode.ACCOUNT_IDLE;
    }

    private void accountWithdrawLoop() {
        // Ask for withdraw amount
        double amount = promptUserDouble("\nHow much would you like to withdraw?\n$", input);
        double target = currentAccount.getBalance() - amount;

        // Prompt again if it goes into the negatives
        if (target < 0.0d) {
            String confirmation = promptUserLine("""
                    Are you sure you wish to withdraw %.2f?
                    This transaction can put your account into the negatives. (Y/N)
                    >""".formatted(amount),
                    input
            );
            if (!confirmation.toLowerCase(Locale.ROOT).equals("y")) {
                mode = AccessMode.ACCOUNT_IDLE;
                return;
            }
        }

        // Deposit amount and continue
        currentAccount.setBalance(target);
        mode = AccessMode.ACCOUNT_IDLE;
    }

    private void accountInfoLoop() {
    }

    private void accountLogoutLoop() {
        // Ask again
        String response = promptUserLine("Are you sure you wish to log out? (Y/N)\n>", input);
        if (response.toLowerCase(Locale.ROOT).equals("y")) {
            // Logout
            currentAccount = null;
            mode = AccessMode.LOGIN_IDLE;
        }
    }

    private void accountCloseLoop() {
        // Ask again
        String response = promptUserLine(
                "Are you SURE you wish to close your account? (Y/N)\n>",
                input
        );
        if (response.toLowerCase(Locale.ROOT).equals("y")) {
            // Find the account in list, then remove
            int index = findAccountID(currentAccount);
            accounts.remove(index);

            // Now logout
            currentAccount = null;
            mode = AccessMode.LOGIN_IDLE;
        }
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
        Account newAccount;

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
            inputName = promptUserLine("\nWhat is your first and last name?\n>", input);

        while (inputAddress == null)
            inputAddress = promptUserLine("\nWhat is your address?\n>", input);

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
        // Declare variables
        double result;

        // Prompt and get input
        System.out.print(prompt);
        try { result = in.nextDouble(); }
        catch (InputMismatchException e) {
            in.nextLine();
            return Double.NaN;
        }

        // Clean buffer and return input
        in.nextLine();
        return result;
    }

    /**
     * Prompts user with provided text and returns the input value as an integer.
     * @param prompt Prompt to print in console
     * @param in Scanner to pull input from
     * @return User's input as integer
     */
    private int promptUserInt(String prompt, Scanner in) {
        // Declare variables
        int result;

        // Prompt and get input
        System.out.print(prompt);
        try { result = in.nextInt(); }
        catch (InputMismatchException e) {
            in.nextLine();
            return Integer.MIN_VALUE;
        }

        // Clean buffer and return input
        in.nextLine();
        return result;
    }

    /**
     * Provides a list of accounts in this manager's
     * list based on the provided name
     * @param name The name that is being searched for
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

    /**
     * Returns the first position of a given account
     * reference from this manager's list of accounts.
     * If no account is found, -1 is returned indicating
     * that the reference could not be located.
     * @param ref Reference to account to find
     * @return The first position of the given account reference
     */
    private int findAccountID(Account ref) {
        for (int i = 0; i < accounts.size(); i++)
            if (accounts.get(i).hashCode() == ref.hashCode()) {
                return i;
            }
        return -1;
    }
}
