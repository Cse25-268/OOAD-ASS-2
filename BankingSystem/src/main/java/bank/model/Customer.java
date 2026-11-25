package bank.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A customer of the bank. A customer can hold many accounts.
 * The employer details are needed if the customer wants a cheque account.
 */
public class Customer extends Person {

    private int id;                  // set by the database
    private String idNumber;         // national id (Omang) number
    private String employerName;
    private String employerAddress;
    private List<Account> accounts = new ArrayList<>();

    public Customer(String firstName, String surname, String idNumber, String address, String phone,
                    String employerName, String employerAddress) {
        super(firstName, surname, address, phone);
        this.idNumber = idNumber;
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getEmployerAddress() {
        return employerAddress;
    }

    public void setEmployerAddress(String employerAddress) {
        this.employerAddress = employerAddress;
    }

    // a customer is working if they gave an employer name
    public boolean isEmployed() {
        return employerName != null && !employerName.trim().isEmpty();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    @Override
    public String toString() {
        return getFullName() + " (" + idNumber + ")";
    }
}
