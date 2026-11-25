package bank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * One line in the transaction history of an account.
 * type is one of DEPOSIT, WITHDRAWAL or INTEREST.
 */
public class Transaction {

    private int id;
    private String accountNumber;
    private String type;
    private double amount;
    private double balanceAfter;
    private String description;
    private LocalDateTime dateTime;

    // used when a new transaction happens (time is "now")
    public Transaction(String accountNumber, String type, double amount, double balanceAfter, String description) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.dateTime = LocalDateTime.now();
    }

    // used when loading an old transaction from the database
    public Transaction(int id, String accountNumber, String type, double amount, double balanceAfter,
                       String description, LocalDateTime dateTime) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    // nicer text for showing in the table
    public String getDateTimeText() {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Override
    public String toString() {
        return getDateTimeText() + " " + type + " BWP" + String.format("%.2f", amount);
    }
}
