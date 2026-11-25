package bank.controller;

import bank.dao.AccountDAO;
import bank.dao.TransactionDAO;
import bank.model.Account;
import bank.model.BankException;
import bank.model.ChequeAccount;
import bank.model.Customer;
import bank.model.InterestBearing;
import bank.model.InvestmentAccount;
import bank.model.SavingsAccount;
import bank.model.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * Sits between the account screen and the Account model / DAOs.
 * The screen never changes a balance itself, it asks this class to do it
 * and this class makes sure the new balance and the transaction are saved.
 */
public class AccountController {

    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public AccountController(AccountDAO accountDAO, TransactionDAO transactionDAO) {
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
    }

    // type must be "SAVINGS", "INVESTMENT" or "CHEQUE"
    public Account openAccount(Customer customer, String type, String branch, double initialDeposit)
            throws BankException {

        if (customer == null) {
            throw new BankException("Please select a customer first");
        }
        if (type == null) {
            throw new BankException("Please select the type of account");
        }
        if (branch == null || branch.trim().isEmpty()) {
            throw new BankException("Please enter the branch");
        }
        if (initialDeposit < 0) {
            throw new BankException("The opening deposit cannot be negative");
        }

        try {
            String number = accountDAO.getNextAccountNumber();
            Account account;

            if (type.equals("SAVINGS")) {
                account = new SavingsAccount(number, customer, branch.trim());
            } else if (type.equals("INVESTMENT")) {
                // this one checks for the BWP500 minimum itself
                account = new InvestmentAccount(number, customer, branch.trim(), initialDeposit);
            } else if (type.equals("CHEQUE")) {
                // this one checks that the customer is working
                account = new ChequeAccount(number, customer, branch.trim());
            } else {
                throw new BankException("Unknown account type: " + type);
            }

            // savings and cheque accounts start with 0 so add the opening deposit
            if (initialDeposit > 0 && account.getBalance() == 0) {
                account.deposit(initialDeposit, "Initial deposit");
            }

            // save the account first and then its transactions
            accountDAO.insert(account);
            for (Transaction t : account.getTransactions()) {
                transactionDAO.insert(t);
            }
            customer.addAccount(account);
            return account;

        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    public void deposit(String accountNumber, double amount) throws BankException {
        try {
            Account account = getAccountOrFail(accountNumber);
            account.deposit(amount);
            saveLatest(account);
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    public void withdraw(String accountNumber, double amount) throws BankException {
        try {
            Account account = getAccountOrFail(accountNumber);
            account.withdraw(amount);     // savings account will throw an exception here
            saveLatest(account);
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    /**
     * Pays the monthly interest into every account that earns interest.
     * Returns the total amount of interest that was paid out.
     */
    public double payMonthlyInterest() throws BankException {
        double total = 0;
        try {
            List<Account> all = accountDAO.findAll();
            for (Account account : all) {
                // polymorphism: only the accounts that implement InterestBearing get interest
                if (account instanceof InterestBearing) {
                    InterestBearing interestAccount = (InterestBearing) account;
                    double before = account.getBalance();
                    interestAccount.payInterest();
                    total = total + (account.getBalance() - before);
                    saveLatest(account);
                }
            }
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public Account findAccount(String accountNumber) throws BankException {
        try {
            return accountDAO.findByNumber(accountNumber);
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    public List<Account> getAccountsFor(Customer customer) throws BankException {
        try {
            return accountDAO.findByCustomer(customer);
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    public List<Account> getAllAccounts() throws BankException {
        try {
            return accountDAO.findAll();
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    public List<Transaction> getTransactions(String accountNumber) throws BankException {
        try {
            return transactionDAO.findByAccount(accountNumber);
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // ---------- helper methods ----------

    private Account getAccountOrFail(String accountNumber) throws SQLException, BankException {
        Account account = accountDAO.findByNumber(accountNumber);
        if (account == null) {
            throw new BankException("Account " + accountNumber + " was not found");
        }
        return account;
    }

    // saves the new balance and the transaction that just happened
    private void saveLatest(Account account) throws SQLException {
        accountDAO.updateBalance(account);
        Transaction last = account.getLastTransaction();
        if (last != null) {
            transactionDAO.insert(last);
        }
    }
}
