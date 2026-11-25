package bank.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract parent of all the account types.
 * An account must always belong to a customer so the constructor
 * does not allow a null customer.
 */
public abstract class Account implements Transactable {

    // private = encapsulation, other classes use the getters
    private String accountNumber;
    private double balance;
    private String branch;
    private LocalDate dateOpened;
    private Customer customer;

    // transactions that happened while the program was running
    private List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, Customer customer, String branch, double balance, LocalDate dateOpened) {
        if (customer == null) {
            throw new IllegalArgumentException("An account cannot exist without a customer");
        }
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.branch = branch;
        this.balance = balance;
        this.dateOpened = dateOpened;
    }

    // ---------- getters ----------

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public String getBranch() {
        return branch;
    }

    public LocalDate getDateOpened() {
        return dateOpened;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    // the most recent transaction (or null if there is none yet)
    public Transaction getLastTransaction() {
        if (transactions.isEmpty()) {
            return null;
        }
        return transactions.get(transactions.size() - 1);
    }

    // each type of account says what it is called
    public abstract String getAccountType();

    // ---------- business logic ----------

    // overloaded method: deposit with a default description
    @Override
    public void deposit(double amount) throws BankException {
        deposit(amount, "Cash deposit");
    }

    // overloaded method: deposit with our own description
    public void deposit(double amount, String description) throws BankException {
        if (amount <= 0) {
            throw new BankException("Deposit amount must be more than zero");
        }
        balance = round(balance + amount);
        addTransaction("DEPOSIT", amount, description);
    }

    // The subclasses decide if withdrawals are allowed and use this to do it
    protected void takeMoney(double amount, String description) throws BankException {
        if (amount <= 0) {
            throw new BankException("Withdrawal amount must be more than zero");
        }
        if (amount > balance) {
            throw new InsufficientFundsException(balance, amount);
        }
        balance = round(balance - amount);
        addTransaction("WITHDRAWAL", amount, description);
    }

    // used by the interest accounts when they pay interest
    protected void addInterest(double interest) {
        if (interest <= 0) {
            return;
        }
        balance = round(balance + interest);
        addTransaction("INTEREST", interest, "Monthly interest");
    }

    private void addTransaction(String type, double amount, String description) {
        transactions.add(new Transaction(accountNumber, type, amount, balance, description));
    }

    // keeps money to 2 decimal places
    protected double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return getAccountType() + " " + accountNumber + " (BWP" + String.format("%.2f", balance) + ")";
    }
}
