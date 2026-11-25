package bank.model;

/**
 * Implemented by the accounts that earn interest
 * (SavingsAccount and InvestmentAccount).
 * The ChequeAccount does not earn interest so it does not implement this.
 */
public interface InterestBearing {

    // the monthly interest rate, e.g. 0.05 means 5%
    double getMonthlyRate();

    // works out how much interest is due this month (does not change the balance)
    double calculateInterest();

    // works out the interest and adds it to the balance
    void payInterest();
}
