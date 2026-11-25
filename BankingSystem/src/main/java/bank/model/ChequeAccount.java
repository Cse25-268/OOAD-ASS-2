package bank.model;

import java.time.LocalDate;

/**
 * Cheque account: used for salaries. Allows deposits and withdrawals
 * of any amount (as long as there is money). Only a working customer
 * (one who has an employer) can open it.
 */
public class ChequeAccount extends Account {

    // for a brand new account
    public ChequeAccount(String accountNumber, Customer customer, String branch) throws BankException {
        super(accountNumber, customer, branch, 0.0, LocalDate.now());
        if (!customer.isEmployed()) {
            throw new BankException("A cheque account can only be opened by a customer who is working. "
                    + "Please add the employer details first.");
        }
    }

    // for loading an existing account from the database
    public ChequeAccount(String accountNumber, Customer customer, String branch, double balance, LocalDate dateOpened) {
        super(accountNumber, customer, branch, balance, dateOpened);
    }

    @Override
    public String getAccountType() {
        return "CHEQUE";
    }

    @Override
    public void withdraw(double amount) throws BankException {
        takeMoney(amount, "Cash withdrawal");
    }

    // extra method only the cheque account has
    public void creditSalary(double amount) throws BankException {
        deposit(amount, "Salary from " + getCustomer().getEmployerName());
    }
}
