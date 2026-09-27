package com.bank;

import java.util.Scanner;

import com.bank.service.BankService;
import com.bank.dao.AccountDAO;
import com.bank.dao.TransactionDAO;
import com.bank.dao.CustomerDAO;
import com.bank.model.Customer;
import com.bank.model.Account;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        BankService bankService = new BankService();
        AccountDAO accountDAO = new AccountDAO();
        TransactionDAO transactionDAO = new TransactionDAO();
        CustomerDAO customerDAO = new CustomerDAO();

        while (true) {

            System.out.println("\n===== BANKING MANAGEMENT SYSTEM =====");
            System.out.println("1. Create Customer");
            System.out.println("2. Create Account");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Check Balance");
            System.out.println("6. Transaction History");
            System.out.println("7. Transfer Money");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {

                System.out.print("Enter customer name: ");
                String name = scanner.nextLine();

                System.out.print("Enter phone number: ");
                String phone = scanner.nextLine();

                System.out.print("Enter email: ");
                String email = scanner.nextLine();

                Customer customer =
                        new Customer(0, name, phone, email);

                customerDAO.addCustomer(customer);

            } else if (choice == 2) {

                System.out.print("Enter account number: ");
                long accountNumber = scanner.nextLong();

                System.out.print("Enter customer ID: ");
                int customerId = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Enter account type: ");
                String accountType = scanner.nextLine();

                System.out.print("Enter initial deposit: ");
                double balance = scanner.nextDouble();

                Account account =
                        new Account(
                                accountNumber,
                                customerId,
                                accountType,
                                balance
                        );

                accountDAO.addAccount(account);

            } else if (choice == 3) {

                System.out.print("Enter account number: ");
                long accountNumber = scanner.nextLong();

                System.out.print("Enter deposit amount: ");
                double amount = scanner.nextDouble();

                bankService.deposit(accountNumber, amount);

            } else if (choice == 4) {

                System.out.print("Enter account number: ");
                long accountNumber = scanner.nextLong();

                System.out.print("Enter withdrawal amount: ");
                double amount = scanner.nextDouble();

                bankService.withdraw(accountNumber, amount);

            } else if (choice == 5) {

                System.out.print("Enter account number: ");
                long accountNumber = scanner.nextLong();

                double balance = accountDAO.getBalance(accountNumber);

                if (balance >= 0) {
                    System.out.println("Current balance: Rs." + balance);
                } else {
                    System.out.println("Account not found!");
                }

            } else if (choice == 6) {

                System.out.print("Enter account number: ");
                long accountNumber = scanner.nextLong();

                transactionDAO.getTransactions(accountNumber);

            } else if (choice == 7) {

                System.out.print("Enter sender account number: ");
                long senderAccount = scanner.nextLong();

                System.out.print("Enter receiver account number: ");
                long receiverAccount = scanner.nextLong();

                System.out.print("Enter transfer amount: ");
                double amount = scanner.nextDouble();

                bankService.transfer(
                        senderAccount,
                        receiverAccount,
                        amount
                );

            } else if (choice == 8) {

                System.out.println("Thank you!");
                break;

            } else {

                System.out.println("Invalid choice!");
            }
        }

        scanner.close();
    }
}