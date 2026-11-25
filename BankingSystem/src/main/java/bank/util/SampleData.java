package bank.util;

import bank.controller.AccountController;
import bank.controller.CustomerController;
import bank.model.BankException;
import bank.model.Customer;

/**
 * Puts 10 sample customers (with accounts) into the database.
 * The assignment says 10 sample records must be in the database
 * when presenting. It skips customers that are already there so it is safe
 * to run more than once.
 */
public class SampleData {

    // firstName, surname, idNumber, address, phone, employer, employerAddress
    private static final String[][] CUSTOMERS = {
            {"Kabelo", "Mosweu", "100100001", "Plot 101 Gaborone", "71000001", "Choppies", "Broadhurst, Gaborone"},
            {"Neo", "Dube", "100100002", "Plot 22 Francistown", "71000002", "Botswana Railways", "Francistown"},
            {"Tebogo", "Sebina", "100100003", "Plot 5 Maun", "71000003", "", ""},
            {"Thato", "Kgosi", "100100004", "Block 6 Gaborone", "71000004", "Water Utilities", "Gaborone"},
            {"Lesedi", "Moatlhodi", "100100005", "Plot 77 Palapye", "71000005", "", ""},
            {"Boitumelo", "Ramotswa", "100100006", "Plot 9 Lobatse", "71000006", "Sefalana", "Lobatse"},
            {"Onkabetse", "Phiri", "100100007", "Plot 310 Kasane", "71000007", "Chobe Lodge", "Kasane"},
            {"Mpho", "Seleka", "100100008", "Plot 18 Serowe", "71000008", "", ""},
            {"Naledi", "Ngwako", "100100009", "Block 3 Molepolole", "71000009", "Mochudi Clinic", "Mochudi"},
            {"Tumelo", "Bogatsu", "100100010", "Plot 64 Gaborone", "71000010", "Orange Botswana", "Gaborone"}
    };

    // Opening deposits for the savings and investment accounts
    private static final double[] SAVINGS_AMOUNTS = {1500, 800, 2500, 300, 1000, 4000, 750, 600, 1200, 5000};
    private static final double[] INVESTMENT_AMOUNTS = {5000, 0, 2000, 0, 750, 10000, 0, 500, 0, 3500};

    // returns how many new customers were added
    public static int load(CustomerController customerController, AccountController accountController)
            throws BankException {

        int added = 0;
        for (int i = 0; i < CUSTOMERS.length; i++) {
            String[] row = CUSTOMERS[i];

            if (customerController.findByIdNumber(row[2]) != null) {
                continue;   // already in the database
            }

            Customer c = customerController.registerCustomer(row[0], row[1], row[2], row[3], row[4], row[5], row[6]);

            // everybody gets a savings account
            accountController.openAccount(c, "SAVINGS", "Main Mall", SAVINGS_AMOUNTS[i]);

            // some customers also get an investment account
            if (INVESTMENT_AMOUNTS[i] >= 500) {
                accountController.openAccount(c, "INVESTMENT", "Main Mall", INVESTMENT_AMOUNTS[i]);
            }

            // customers who are working also get a cheque account
            if (c.isEmployed()) {
                accountController.openAccount(c, "CHEQUE", "Main Mall", 2000);
            }
            added++;
        }
        return added;
    }
}
