package bank.model;

import java.time.LocalDate;

/**
 * Investment account: pays 5% interest every month, allows deposits
 * and withdrawals. Can only be opened with at least BWP500.00.
 */
public class InvestmentAccount extends Account implements InterestBearing {

    public static final double MINIMUM_OPENING_AMOUNT = 500.00;
    private static final double MONTHLY_RATE = 0.05;     // 5%

    // for a brand new account, needs the opening deposit
    public InvestmentAccount(String accountNumber, Customer customer, String branch, double initialDeposit)
            throws BankException {
        super(accountNumber, customer, branch, 0.0, LocalDate.now());
        if (initialDeposit < MINIMUM_OPENING_AMOUNT) {
            throw new BankException("An investment account needs an opening deposit of at least BWP"
                    + String.format("%.2f", MINIMUM_OPENING_AMOUNT));
        }
        deposit(initialDeposit, "Initial deposit");
    }

    // for loading an existing account from the database
    // (no minimum check here because the balance may have gone down since opening)
    public InvestmentAccount(String accountNumber, Customer customer, String branch, double balance, LocalDate dateOpened) {
        super(accountNumber, customer, branch, balance, dateOpened);
    }

    @Override
    public String getAccountType() {
        return "INVESTMENT";
    }

    @Override
    public void withdraw(double amount) throws BankException {
        takeMoney(amount, "Cash withdrawal");
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
