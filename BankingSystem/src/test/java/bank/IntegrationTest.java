package bank;

import bank.controller.AccountController;
import bank.controller.CustomerController;
import bank.controller.LoginController;
import bank.dao.AccountDAO;
import bank.dao.CustomerDAO;
import bank.dao.Database;
import bank.dao.TransactionDAO;
import bank.dao.UserDAO;
import bank.model.Account;
import bank.model.BankException;
import bank.model.Customer;
import bank.util.SampleData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests: controller -> model -> DAO -> database all working together.
 * The GUI classes only call the controllers, so these tests do the same thing
 * the screens would do, but without needing to click on a window.
 * An in-memory SQLite database is used so nothing is left over after the tests.
 */
public class IntegrationTest {

    private Database db;
    private LoginController loginController;
    private CustomerController customerController;
    private AccountController accountController;

    @BeforeEach
    void setUp() throws Exception {
        db = new Database("jdbc:sqlite::memory:");
        UserDAO userDAO = new UserDAO(db);
        CustomerDAO customerDAO = new CustomerDAO(db);
        AccountDAO accountDAO = new AccountDAO(db, customerDAO);
        TransactionDAO transactionDAO = new TransactionDAO(db);

        loginController = new LoginController(userDAO);
        customerController = new CustomerController(customerDAO, accountDAO);
        accountController = new AccountController(accountDAO, transactionDAO);
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    @Test
    void loginWithCorrectAndWrongPassword() throws BankException {
        assertTrue(loginController.login("admin", "admin123"));
        assertEquals("admin", loginController.getLoggedInUser());

        loginController.logout();
        assertFalse(loginController.login("admin", "wrong"));
        assertFalse(loginController.isLoggedIn());
    }

    @Test
    void fullUserFlow() throws BankException {
        // 1. staff member logs in
        assertTrue(loginController.login("admin", "admin123"));

        // 2. register a working customer
        Customer c = customerController.registerCustomer("Thato", "Kgosi", "555000111",
                "Block 6 Gaborone", "71111111", "Water Utilities", "Gaborone");
        assertTrue(c.getId() > 0);

        // 3. open all three types of account
        Account savings = accountController.openAccount(c, "SAVINGS", "Main Mall", 1000);
        Account investment = accountController.openAccount(c, "INVESTMENT", "Main Mall", 2000);
        Account cheque = accountController.openAccount(c, "CHEQUE", "Main Mall", 300);

        // 4. deposits and a withdrawal
        accountController.deposit(cheque.getAccountNumber(), 700);
        accountController.withdraw(cheque.getAccountNumber(), 200);

        // 5. pay the monthly interest
        double paid = accountController.payMonthlyInterest();
        assertEquals(100.50, paid, 0.001);   // 0.50 on savings + 100.00 on investment

        // 6. read everything back from the database and check it
        Account savingsFromDb = accountController.findAccount(savings.getAccountNumber());
        Account investmentFromDb = accountController.findAccount(investment.getAccountNumber());
        Account chequeFromDb = accountController.findAccount(cheque.getAccountNumber());

        assertEquals(1000.50, savingsFromDb.getBalance(), 0.001);
        assertEquals(2100.00, investmentFromDb.getBalance(), 0.001);
        assertEquals(800.00, chequeFromDb.getBalance(), 0.001);

        // 7. check the transaction history was saved
        assertEquals(2, accountController.getTransactions(savings.getAccountNumber()).size());     // deposit + interest
        assertEquals(2, accountController.getTransactions(investment.getAccountNumber()).size());  // deposit + interest
        assertEquals(3, accountController.getTransactions(cheque.getAccountNumber()).size());      // 300, +700, -200
    }

    @Test
    void customerCanHaveMultipleAccounts() throws BankException {
        Customer c = customerController.registerCustomer("Neo", "Dube", "555000222",
                "Maun", "72222222", "Sefalana", "Maun");
        accountController.openAccount(c, "SAVINGS", "Main Mall", 100);
        accountController.openAccount(c, "INVESTMENT", "Main Mall", 600);
        accountController.openAccount(c, "CHEQUE", "Main Mall", 0);

        List<Account> accounts = accountController.getAccountsFor(c);
        assertEquals(3, accounts.size());
    }

    @Test
    void savingsWithdrawalIsRejectedAndBalanceStays() throws BankException {
        Customer c = customerController.registerCustomer("Tebogo", "Sebina", "555000333",
                "Maun", "73333333", "", "");
        Account savings = accountController.openAccount(c, "SAVINGS", "Main Mall", 500);

        assertThrows(BankException.class, () -> accountController.withdraw(savings.getAccountNumber(), 100));
        assertEquals(500.0, accountController.findAccount(savings.getAccountNumber()).getBalance(), 0.001);
    }

    @Test
    void investmentBelowMinimumIsRejectedAndNothingSaved() throws BankException {
        Customer c = customerController.registerCustomer("Lesedi", "Moatlhodi", "555000444",
                "Palapye", "74444444", "", "");

        assertThrows(BankException.class, () -> accountController.openAccount(c, "INVESTMENT", "Main Mall", 100));
        assertEquals(0, accountController.getAccountsFor(c).size());
    }

    @Test
    void chequeAccountRefusedForCustomerWhoIsNotWorking() throws BankException {
        Customer c = customerController.registerCustomer("Mpho", "Seleka", "555000555",
                "Serowe", "75555555", "", "");

        assertThrows(BankException.class, () -> accountController.openAccount(c, "CHEQUE", "Main Mall", 0));

        // after adding employer details it should work
        customerController.updateEmployer(c, "Orange Botswana", "Gaborone");
        Account cheque = accountController.openAccount(c, "CHEQUE", "Main Mall", 0);
        assertNotNull(cheque);
    }

    @Test
    void duplicateIdNumberIsRejected() throws BankException {
        customerController.registerCustomer("A", "B", "555000666", "Gaborone", "", "", "");
        assertThrows(BankException.class,
                () -> customerController.registerCustomer("C", "D", "555000666", "Gaborone", "", "", ""));
    }

    @Test
    void sampleDataLoadsTenCustomers() throws BankException {
        int added = SampleData.load(customerController, accountController);
        assertEquals(10, added);
        assertEquals(10, customerController.getCustomers().size());

        // running it again must not add duplicates
        assertEquals(0, SampleData.load(customerController, accountController));
    }
}
