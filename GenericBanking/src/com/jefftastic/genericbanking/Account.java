package com.jefftastic.genericbanking;

/**
 *  Holds information about an account, such as:<br>
 *  - First Name<br>
 *  - Last Name<br>
 *  - Address<br>
 *  - and Balance<br>
 */

public class Account {
    private String name, address;
    private double balance;

    /**
     * Constructs a new Account object based on the provided
     * args.
     * @param accName The name of the account
     * @param accAddress The address associated with the account
     * @param accBalance The starting balance of the account
     */
    Account(String accName, String accAddress, double accBalance) {
        this.name = accName;
        this.address = accAddress;
        this.balance = accBalance;
    }

    /**
     * @return The name of this account
     */
    public String getName() {return this.name;}

    /**
     * @return The address associated with this account
     */
    public String getAddress() {return this.address;}

    /**
     * @return The current balance of this account
     */
    public double getBalance() {return this.balance;}

    /**
     * Sets the balance of this account.
     * @param newBalance The new balance.
     */
    public void setBalance(double newBalance) {
        this.balance = newBalance;
    }
}
