package bank.model;

import java.time.LocalDate;

/**
 * Savings account: pays 0.05% interest every month and
 * does NOT allow withdrawals.
 */
public class SavingsAccount extends Account implements InterestBearing {

    private static final double MONTHLY_RATE = 0.0005;   // 0.05%

    // for a brand new account
    public SavingsAccount(String accountNumber, Customer customer, String branch) {
        super(accountNumber, customer, branch, 0.0, LocalDate.now());
    }

    // for loading an existing account from the database
    public SavingsAccount(String accountNumber, Customer customer, String branch, double balance, LocalDate dateOpened) {
        super(accountNumber, customer, branch, balance, dateOpened);
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    // overriding: savings accounts never allow a withdrawal
    @Override
    public void withdraw(double amount) throws BankException {
        throw new BankException("A savings account does not allow withdrawals");
    }

    @Override
    public double getMonthlyRate() {
        return MONTHLY_RATE;
    }

    @Override
    public double calculateInterest() {
        return round(getBalance() * MONTHLY_RATE);
    }

    @Override
    public void payInterest() {
        addInterest(calculateInterest());
    }
}
