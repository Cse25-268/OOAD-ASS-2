package bank;

import bank.model.BankException;
import bank.model.ChequeAccount;
import bank.model.Customer;
import bank.model.InsufficientFundsException;
import bank.model.InvestmentAccount;
import bank.model.SavingsAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the core model classes (no database or GUI involved).
 */
public class ModelTest {

    private Customer working;
    private Customer notWorking;

    @BeforeEach
    void setUp() {
        working = new Customer("Kabelo", "Mosweu", "123456789", "Gaborone", "71234567", "Choppies", "Gaborone");
        notWorking = new Customer("Neo", "Dube", "987654321", "Maun", "72345678", "", "");
    }

    @Test
    void savingsDepositWorks() throws BankException {
        SavingsAccount a = new SavingsAccount("ACC1", working, "Main Mall");
        a.deposit(1000);
        assertEquals(1000.0, a.getBalance(), 0.001);
        assertEquals(1, a.getTransactions().size());
    }

    @Test
    void savingsDoesNotAllowWithdrawal() throws BankException {
        SavingsAccount a = new SavingsAccount("ACC1", working, "Main Mall");
        a.deposit(1000);
        assertThrows(BankException.class, () -> a.withdraw(100));
        assertEquals(1000.0, a.getBalance(), 0.001);
    }

    @Test
    void savingsInterestIsPointZeroFivePercent() throws BankException {
        SavingsAccount a = new SavingsAccount("ACC1", working, "Main Mall");
        a.deposit(2000);
        a.payInterest();
        assertEquals(2001.0, a.getBalance(), 0.001);   // 0.05% of 2000 = 1.00
    }

    @Test
    void investmentNeedsMinimumOpeningDeposit() {
        assertThrows(BankException.class, () -> new InvestmentAccount("ACC2", working, "Main Mall", 499.99));
    }

    @Test
    void investmentInterestIsFivePercent() throws BankException {
        InvestmentAccount a = new InvestmentAccount("ACC2", working, "Main Mall", 1000);
        a.payInterest();
        assertEquals(1050.0, a.getBalance(), 0.001);
    }

    @Test
    void investmentWithdrawalWorksButNotMoreThanBalance() throws BankException {
        InvestmentAccount a = new InvestmentAccount("ACC2", working, "Main Mall", 1000);
        a.withdraw(400);
        assertEquals(600.0, a.getBalance(), 0.001);
        assertThrows(InsufficientFundsException.class, () -> a.withdraw(5000));
    }

    @Test
    void chequeAccountOnlyForWorkingCustomers() throws BankException {
        ChequeAccount ok = new ChequeAccount("ACC3", working, "Main Mall");
        assertTrue(ok.getBalance() == 0);
        assertThrows(BankException.class, () -> new ChequeAccount("ACC4", notWorking, "Main Mall"));
    }

    @Test
    void negativeDepositIsRejected() {
        SavingsAccount a = new SavingsAccount("ACC1", working, "Main Mall");
        assertThrows(BankException.class, () -> a.deposit(-50));
    }

    @Test
    void accountCannotExistWithoutCustomer() {
        assertThrows(IllegalArgumentException.class, () -> new SavingsAccount("ACC9", null, "Main Mall"));
    }
}
