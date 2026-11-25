package bank.model;

/**
 * Anything that money can be put into and taken out of.
 */
public interface Transactable {

    void deposit(double amount) throws BankException;

    void withdraw(double amount) throws BankException;
}
