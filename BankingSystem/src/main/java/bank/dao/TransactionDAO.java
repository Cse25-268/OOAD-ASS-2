package bank.dao;

import bank.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for the transactions table.
 * Transactions are only ever added and read (never changed).
 */
public class TransactionDAO {

    private Connection conn;

    public TransactionDAO(Database database) {
        this.conn = database.getConnection();
    }

    // CREATE
    public void insert(Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions (account_number, type, amount, balance_after, description, date_time) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getAccountNumber());
            ps.setString(2, t.getType());
            ps.setDouble(3, t.getAmount());
            ps.setDouble(4, t.getBalanceAfter());
            ps.setString(5, t.getDescription());
            ps.setString(6, t.getDateTime().toString());
            ps.executeUpdate();
        }
    }

    // READ all the transactions of one account (oldest first)
    public List<Transaction> findByAccount(String accountNumber) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(
                            rs.getInt("id"),
                            rs.getString("account_number"),
                            rs.getString("type"),
                            rs.getDouble("amount"),
                            rs.getDouble("balance_after"),
                            rs.getString("description"),
                            LocalDateTime.parse(rs.getString("date_time"))));
                }
            }
        }
        return list;
    }

    // how many transactions an account has
    public int countByAccount(String accountNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
