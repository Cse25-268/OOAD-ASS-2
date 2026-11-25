package bank.dao;

import bank.model.Account;
import bank.model.ChequeAccount;
import bank.model.Customer;
import bank.model.InvestmentAccount;
import bank.model.SavingsAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for the accounts table.
 * The type column says which kind of account the row is
 * (SAVINGS, INVESTMENT or CHEQUE).
 */
public class AccountDAO {

    private Connection conn;
    private CustomerDAO customerDAO;

    public AccountDAO(Database database, CustomerDAO customerDAO) {
        this.conn = database.getConnection();
        this.customerDAO = customerDAO;
    }

    // CREATE
    public void insert(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, customer_id, type, branch, balance, date_opened) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getAccountNumber());
            ps.setInt(2, account.getCustomer().getId());
            ps.setString(3, account.getAccountType());
            ps.setString(4, account.getBranch());
            ps.setDouble(5, account.getBalance());
            ps.setString(6, account.getDateOpened().toString());
            ps.executeUpdate();
        }
    }

    // READ one
    public Account findByNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer owner = customerDAO.findById(rs.getInt("customer_id"));
                    return makeAccount(rs, owner);
                }
            }
        }
        return null;
    }

    // READ all the accounts of one customer
    public List<Account> findByCustomer(Customer customer) throws SQLException {
        List<Account> list = new ArrayList<>();
        customer.getAccounts().clear();   // so we do not add the same account twice

        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY account_number";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customer.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Account a = makeAccount(rs, customer);
                    customer.addAccount(a);
                    list.add(a);
                }
            }
        }
        return list;
    }

    // READ all accounts in the bank
    public List<Account> findAll() throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY account_number";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Customer owner = customerDAO.findById(rs.getInt("customer_id"));
                list.add(makeAccount(rs, owner));
            }
        }
        return list;
    }

    // UPDATE (only the balance changes after an account is opened)
    public void updateBalance(Account account) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, account.getBalance());
            ps.setString(2, account.getAccountNumber());
            ps.executeUpdate();
        }
    }

    // DELETE (the transactions of the account are deleted first)
    public void delete(String accountNumber) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM transactions WHERE account_number = ?")) {
            ps.setString(1, accountNumber);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM accounts WHERE account_number = ?")) {
            ps.setString(1, accountNumber);
            ps.executeUpdate();
        }
    }

    // Makes the next account number, e.g. ACC1001, ACC1002 ...
    public String getNextAccountNumber() throws SQLException {
        int next = 1001;
        String sql = "SELECT MAX(CAST(SUBSTR(account_number, 4) AS INTEGER)) FROM accounts";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                next = rs.getInt(1) + 1;
            }
        }
        return "ACC" + next;
    }

    // Looks at the type column and makes the right kind of account object
    private Account makeAccount(ResultSet rs, Customer owner) throws SQLException {
        String number = rs.getString("account_number");
        String type = rs.getString("type");
        String branch = rs.getString("branch");
        double balance = rs.getDouble("balance");
        LocalDate opened = LocalDate.parse(rs.getString("date_opened"));

        if (type.equals("SAVINGS")) {
            return new SavingsAccount(number, owner, branch, balance, opened);
        } else if (type.equals("INVESTMENT")) {
            return new InvestmentAccount(number, owner, branch, balance, opened);
        } else {
            return new ChequeAccount(number, owner, branch, balance, opened);
        }
    }
}
