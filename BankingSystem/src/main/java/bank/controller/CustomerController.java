package bank.controller;

import bank.dao.AccountDAO;
import bank.dao.CustomerDAO;
import bank.model.BankException;
import bank.model.Customer;

import java.sql.SQLException;
import java.util.List;

/**
 * Sits between the customer screen and the Customer model / CustomerDAO.
 * It checks the input before anything is saved.
 */
public class CustomerController {

    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;

    public CustomerController(CustomerDAO customerDAO, AccountDAO accountDAO) {
        this.customerDAO = customerDAO;
        this.accountDAO = accountDAO;
    }

    public Customer registerCustomer(String firstName, String surname, String idNumber, String address,
                                     String phone, String employerName, String employerAddress)
            throws BankException {

        // validate the input
        if (isEmpty(firstName) || isEmpty(surname) || isEmpty(idNumber)) {
            throw new BankException("First name, surname and ID number are required");
        }
        if (isEmpty(address)) {
            throw new BankException("Please enter the customer's address");
        }
        // if they gave an employer name they must also give where it is
        if (!isEmpty(employerName) && isEmpty(employerAddress)) {
            throw new BankException("Please enter the employer's address as well");
        }

        try {
            if (customerDAO.findByIdNumber(idNumber.trim()) != null) {
                throw new BankException("A customer with ID number " + idNumber.trim() + " already exists");
            }
            Customer customer = new Customer(firstName.trim(), surname.trim(), idNumber.trim(),
                    address.trim(), phone == null ? "" : phone.trim(),
                    employerName == null ? "" : employerName.trim(),
                    employerAddress == null ? "" : employerAddress.trim());
            customerDAO.insert(customer);
            return customer;
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    public List<Customer> getCustomers() throws BankException {
        try {
            return customerDAO.findAll();
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    public Customer findByIdNumber(String idNumber) throws BankException {
        try {
            return customerDAO.findByIdNumber(idNumber);
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // update the employer details (needed before a customer can get a cheque account)
    public void updateEmployer(Customer customer, String employerName, String employerAddress) throws BankException {
        if (isEmpty(employerName) || isEmpty(employerAddress)) {
            throw new BankException("Employer name and address are both required");
        }
        try {
            customer.setEmployerName(employerName.trim());
            customer.setEmployerAddress(employerAddress.trim());
            customerDAO.update(customer);
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    public void deleteCustomer(Customer customer) throws BankException {
        try {
            if (!accountDAO.findByCustomer(customer).isEmpty()) {
                throw new BankException("This customer still has accounts so they cannot be deleted");
            }
            customerDAO.delete(customer.getId());
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }
}
