package bank.dao;

import bank.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for the customers table (CRUD operations).
 */
public class CustomerDAO {

    private Connection conn;

    public CustomerDAO(Database database) {
        this.conn = database.getConnection();
    }

    // CREATE
    public void insert(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (first_name, surname, id_number, address, phone, "
                + "employer_name, employer_address) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getSurname());
            ps.setString(3, customer.getIdNumber());
            ps.setString(4, customer.getAddress());
            ps.setString(5, customer.getPhone());
            ps.setString(6, customer.getEmployerName());
            ps.setString(7, customer.getEmployerAddress());
            ps.executeUpdate();

            // get the id that the database made for this customer
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    customer.setId(keys.getInt(1));
                }
            }
        }
    }

    // READ one
    public Customer findById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return makeCustomer(rs);
                }
            }
        }
        return null;
    }

    // READ one (by national id number)
    public Customer findByIdNumber(String idNumber) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return makeCustomer(rs);
                }
            }
        }
        return null;
    }

    // READ all
    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY surname, first_name";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(makeCustomer(rs));
            }
        }
        return list;
    }

    // UPDATE
    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET first_name = ?, surname = ?, address = ?, phone = ?, "
                + "employer_name = ?, employer_address = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getSurname());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getEmployerName());
            ps.setString(6, customer.getEmployerAddress());
            ps.setInt(7, customer.getId());
            ps.executeUpdate();
        }
    }

    // DELETE
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // turns one row of the result into a Customer object
    private Customer makeCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer(
                rs.getString("first_name"),
                rs.getString("surname"),
                rs.getString("id_number"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getString("employer_name"),
                rs.getString("employer_address"));
        c.setId(rs.getInt("id"));
        return c;
    }
}
