# Integration Test Report

**Module:** CSE202 Object Oriented Analysis & Design with Java
**Student:** _______________  **Student number:** _______________
**Date tested:** _______________

## 1. Purpose

To check that the Boundary (JavaFX) classes, Controller classes, core Model classes
and the JDBC data access classes work together as one application.

## 2. How the tests were run

* Automated: `mvn test` runs `ModelTest` and `IntegrationTest`.
  `IntegrationTest` uses an in-memory SQLite database. It calls the same controller
  methods that the screens call.
* Manual: `mvn javafx:run`, login as admin / admin123 and follow the steps in section 4.

## 3. Automated tests

Fill in the Result column after running `mvn test` (PASS / FAIL).

| # | Test | What it checks | Expected | Result |
|---|------|----------------|----------|--------|
| 1 | loginWithCorrectAndWrongPassword | Login through LoginController and UserDAO | admin/admin123 accepted, wrong password rejected | |
| 2 | fullUserFlow | Login, register customer, open 3 accounts, deposit, withdraw, pay interest, read back from DB | Savings 1000.50, Investment 2100.00, Cheque 800.00; 2/2/3 transactions saved | |
| 3 | customerCanHaveMultipleAccounts | One customer with 3 accounts | 3 accounts loaded from DB | |
| 4 | savingsWithdrawalIsRejectedAndBalanceStays | Savings no-withdrawal rule | BankException, balance unchanged | |
| 5 | investmentBelowMinimumIsRejectedAndNothingSaved | BWP500 minimum | BankException, no account saved | |
| 6 | chequeAccountRefusedForCustomerWhoIsNotWorking | Cheque rule and employer update | Refused first, allowed after employer added | |
| 7 | duplicateIdNumberIsRejected | Unique ID number | BankException | |
| 8 | sampleDataLoadsTenCustomers | 10 sample records | 10 added the first time, 0 the second time | |
| 9 | ModelTest (9 tests) | Interest rates, deposit/withdraw rules, null customer | All pass | |

## 4. Manual GUI test (full user flow)

| Step | Action in the program | Expected | Result |
|------|----------------------|----------|--------|
| 1 | Login with admin / admin123 | Main window opens | |
| 2 | Login with a wrong password | "Wrong username or password" | |
| 3 | Press Load 10 Sample Records | 10 customers appear in the table | |
| 4 | Register a new customer with an employer | Customer appears in the table | |
| 5 | Accounts tab: open a Savings account with BWP1000 | Account appears with balance 1000.00 | |
| 6 | Try to withdraw from the Savings account | Error message shown | |
| 7 | Open an Investment account with BWP100 | Error: minimum is BWP500 | |
| 8 | Open an Investment account with BWP1000 | Account appears | |
| 9 | Deposit BWP500 and withdraw BWP200 on the Investment account | Balance 1300.00, 3 transactions in history | |
| 10 | Press Pay Monthly Interest | Balances increase, INTEREST transactions added | |
| 11 | Close the program and open it again | Data is still there (saved in bank.db) | |

## 5. Problems found and fixes

(Write down anything that failed and what you changed.)

## 6. Conclusion

(Write a short conclusion after running the tests.)
