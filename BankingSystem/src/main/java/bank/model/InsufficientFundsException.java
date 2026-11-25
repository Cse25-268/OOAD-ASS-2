package bank.model;

/**
 * Thrown when someone tries to withdraw more money than is in the account.
 */
public class InsufficientFundsException extends BankException {

    public InsufficientFundsException(double balance, double amount) {
        super("Insufficient funds. Balance is BWP" + String.format("%.2f", balance)
                + " but you tried to withdraw BWP" + String.format("%.2f", amount));
    }
}
