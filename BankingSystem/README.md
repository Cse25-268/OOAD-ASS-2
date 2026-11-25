# CSE202 Banking System (Part B)

Object Oriented Analysis & Design with Java - BSc Computer Systems Engineering

A small banking application written in Java. Staff log in, register customers,
open Savings / Investment / Cheque accounts, deposit, withdraw and pay monthly
interest. Data is saved in an SQLite database file (`bank.db`).

**Student name:** _______________  **Student number:** _______________

## How to run (GitHub Codespaces or local)

You need Java 17+ and Maven.

```
mvn test            # runs the unit tests and the integration tests
mvn javafx:run      # starts the program
```

Login: **admin / admin123**

After logging in, press **Load 10 Sample Records** to put 10 customers into
the database (needed for the presentation).

If the JavaFX window does not open in Codespaces, use the desktop view
(the "Ports" / "Desktop" option) because JavaFX needs a display.

## Project structure

```
src/main/java/bank
 |-- model        core classes (no GUI, no database)
 |-- dao          JDBC classes (Database, UserDAO, CustomerDAO, AccountDAO, TransactionDAO)
 |-- controller   LoginController, CustomerController, AccountController
 |-- view         JavaFX boundary classes (LoginView, MainView, CustomerView, AccountView)
 |-- util         SampleData
 `-- Main.java    starts everything

src/test/java/bank
 |-- ModelTest.java         unit tests for the model
 `-- IntegrationTest.java   controller + model + database tests
```

## Class overview

```
Person (abstract)
  `-- Customer  --- has many --->  Account

Account (abstract) implements Transactable
  |-- SavingsAccount     implements InterestBearing   (0.05% monthly, no withdrawals)
  |-- InvestmentAccount  implements InterestBearing   (5% monthly, min BWP500 to open)
  `-- ChequeAccount                                   (customer must be working)

Transaction   (DEPOSIT / WITHDRAWAL / INTEREST)
```

## OOP principles used

| Principle | Where |
|---|---|
| Abstraction | `Person` and `Account` are abstract classes |
| Inheritance | `Customer extends Person`; Savings/Investment/Cheque `extends Account` |
| Interfaces | `Transactable`, `InterestBearing` |
| Polymorphism | `AccountController.payMonthlyInterest()` loops over all accounts and calls `payInterest()` on the ones that are `InterestBearing`; `withdraw()` behaves differently for each account type |
| Overriding | `withdraw()`, `getAccountType()`, `calculateInterest()`, `toString()` |
| Overloading | `Account.deposit(double)` and `Account.deposit(double, String)`; the two constructors in each account class |
| Encapsulation | all attributes are private with getters/setters |
| Custom exceptions | `BankException`, `InsufficientFundsException` |

## Layers (separation of concerns)

* **View** classes only show things and collect input. They only call controllers.
* **Controller** classes validate the input and tell the model and DAOs what to do.
* **Model** classes hold the banking rules (interest rates, minimum deposit, no savings withdrawals, etc).
* **DAO** classes contain all the JDBC / SQL code.

## Database tables

* `users (username, password)` - passwords are stored as SHA-256 hashes
* `customers (id, first_name, surname, id_number, address, phone, employer_name, employer_address)`
* `accounts (account_number, customer_id, type, branch, balance, date_opened)`
* `transactions (id, account_number, type, amount, balance_after, description, date_time)`

## Assignment requirements checklist

| Requirement | Done in |
|---|---|
| Open an account | `AccountController.openAccount`, Accounts tab |
| Deposit into any account | `AccountController.deposit` |
| Interest 5% Investment / 0.05% Savings | `InvestmentAccount`, `SavingsAccount`, `payMonthlyInterest` |
| Customer with multiple accounts | `Customer.accounts`, test `customerCanHaveMultipleAccounts` |
| Account cannot exist without customer | `Account` constructor |
| Investment opening deposit BWP500 | `InvestmentAccount` constructor |
| Cheque account only for working people | `ChequeAccount` constructor + employer fields |
| 10 sample records | `SampleData` |

## Git commands I used

```
git init
git add .
git commit -m "Core model, GUI, controllers, DAO and tests"
git branch -M main
git remote add origin https://github.com/<my-username>/banking-system.git
git push -u origin main
```
