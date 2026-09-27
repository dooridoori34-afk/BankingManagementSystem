BANKING MANAGEMENT SYSTEM
=========================

DESCRIPTION
-----------
A Java-based Banking Management System that allows customers to
create accounts and perform basic banking operations using a
MySQL database.

TECHNOLOGIES USED
-----------------
- Java
- MySQL
- JDBC
- VS Code
- MySQL Workbench

FEATURES
--------
1. Create Customer
2. Create Account
3. Deposit Money
4. Withdraw Money
5. Check Account Balance
6. View Transaction History
7. Transfer Money Between Accounts
8. Input Validation

PROJECT STRUCTURE
-----------------
BankingManagementSystem
|
|-- src
|   |-- com
|       |-- bank
|           |-- Main.java
|           |
|           |-- model
|           |   |-- Customer.java
|           |   |-- Account.java
|           |   |-- Transaction.java
|           |
|           |-- dao
|           |   |-- CustomerDAO.java
|           |   |-- AccountDAO.java
|           |   |-- TransactionDAO.java
|           |
|           |-- service
|           |   |-- BankService.java
|           |
|           |-- util
|               |-- DBConnection.java
|
|-- lib
|   |-- mysql-connector-j-26.7.0.jar
|
|-- .vscode
|   |-- settings.json
|
|-- README.txt

ARCHITECTURE
------------
The project follows a simple layered architecture.

1. MODEL
   Stores data using Java classes.

2. DAO
   Handles database operations such as INSERT and SELECT.

3. SERVICE
   Contains banking business logic such as deposit,
   withdrawal and money transfer.

4. UTIL
   Handles MySQL database connection.

5. MAIN
   Provides the menu-driven user interface.

DATABASE
--------
Database Name:
banking_system

Tables:
- customer
- account
- transaction_history

BANKING OPERATIONS
------------------
Deposit:
Adds money to an account and records the transaction.

Withdrawal:
Withdraws money only when sufficient balance is available
and records the transaction.

Transfer:
Transfers money from one account to another and records
TRANSFER_OUT and TRANSFER_IN transactions.

Transaction Management:
Money transfer uses JDBC transaction handling with
commit and rollback to maintain database consistency.

VALIDATION
----------
- Deposit amount must be greater than zero.
- Withdrawal amount must be greater than zero.
- Transfer amount must be greater than zero.
- Sender and receiver accounts cannot be the same.
- Withdrawal and transfer require sufficient balance.
- Invalid account numbers are handled.

JDBC CONCEPTS USED
------------------
- Connection
- PreparedStatement
- ResultSet
- executeUpdate()
- executeQuery()
- commit()
- rollback()
- setAutoCommit(false)

OOP CONCEPTS USED
-----------------
- Classes and Objects
- Encapsulation
- Constructors
- Methods
- Packages

HOW TO RUN
----------
1. Create the MySQL database named banking_system.
2. Create the required tables.
3. Configure the MySQL username and password in DBConnection.java.
4. Make sure mysql-connector-j-26.7.0.jar is included.
5. Open the project in VS Code.
6. Run Main.java using the Java extension.

AUTHOR
------
Dooriga