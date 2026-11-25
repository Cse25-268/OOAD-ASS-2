package bank.model;

/**
 * General exception for any problem with a banking rule
 * (e.g. withdrawing from a savings account, bad amount, etc).
 */
public class BankException extends Exception {

    public BankException(String message) {
        super(message);
    }
}
