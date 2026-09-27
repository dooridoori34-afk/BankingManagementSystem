package com.bank.service;

import com.bank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class BankService {

    public void deposit(long accountNumber, double amount) {

        if (amount <= 0) {
            System.out.println("Deposit amount must be greater than 0!");
            return;
        }

        String updateSql = "UPDATE account " +
                           "SET balance = balance + ? " +
                           "WHERE account_number = ?";

        String transactionSql = "INSERT INTO transaction_history " +
                                "(account_number, transaction_type, amount) " +
                                "VALUES (?, 'DEPOSIT', ?)";

        try {
            Connection connection = DBConnection.getConnection();

            PreparedStatement updateStatement =
                    connection.prepareStatement(updateSql);

            updateStatement.setDouble(1, amount);
            updateStatement.setLong(2, accountNumber);

            int rows = updateStatement.executeUpdate();

            if (rows > 0) {

                PreparedStatement transactionStatement =
                        connection.prepareStatement(transactionSql);

                transactionStatement.setLong(1, accountNumber);
                transactionStatement.setDouble(2, amount);

                transactionStatement.executeUpdate();

                System.out.println("Deposit successful!");

            } else {

                System.out.println("Account not found!");
            }

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void withdraw(long accountNumber, double amount) {

        if (amount <= 0) {
            System.out.println("Withdrawal amount must be greater than 0!");
            return;
        }

        String updateSql = "UPDATE account " +
                           "SET balance = balance - ? " +
                           "WHERE account_number = ? " +
                           "AND balance >= ?";

        String transactionSql = "INSERT INTO transaction_history " +
                                "(account_number, transaction_type, amount) " +
                                "VALUES (?, 'WITHDRAW', ?)";

        try {
            Connection connection = DBConnection.getConnection();

            PreparedStatement updateStatement =
                    connection.prepareStatement(updateSql);

            updateStatement.setDouble(1, amount);
            updateStatement.setLong(2, accountNumber);
            updateStatement.setDouble(3, amount);

            int rows = updateStatement.executeUpdate();

            if (rows > 0) {

                PreparedStatement transactionStatement =
                        connection.prepareStatement(transactionSql);

                transactionStatement.setLong(1, accountNumber);
                transactionStatement.setDouble(2, amount);

                transactionStatement.executeUpdate();

                System.out.println("Withdrawal successful!");

            } else {

                System.out.println(
                    "Insufficient balance or account not found!"
                );
            }

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void transfer(long senderAccount,
                         long receiverAccount,
                         double amount) {

        if (amount <= 0) {
            System.out.println(
                "Transfer amount must be greater than 0!"
            );
            return;
        }

        if (senderAccount == receiverAccount) {
            System.out.println(
                "Sender and receiver accounts cannot be the same!"
            );
            return;
        }

        String withdrawSql = "UPDATE account " +
                             "SET balance = balance - ? " +
                             "WHERE account_number = ? " +
                             "AND balance >= ?";

        String depositSql = "UPDATE account " +
                            "SET balance = balance + ? " +
                            "WHERE account_number = ?";

        String transactionSql = "INSERT INTO transaction_history " +
                                "(account_number, transaction_type, amount) " +
                                "VALUES (?, ?, ?)";

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            // Start database transaction
            connection.setAutoCommit(false);

            // Deduct money from sender
            PreparedStatement withdrawStatement =
                    connection.prepareStatement(withdrawSql);

            withdrawStatement.setDouble(1, amount);
            withdrawStatement.setLong(2, senderAccount);
            withdrawStatement.setDouble(3, amount);

            int withdrawn = withdrawStatement.executeUpdate();

            if (withdrawn == 0) {

                connection.rollback();

                System.out.println(
                    "Transfer failed: insufficient balance " +
                    "or account not found!"
                );

                return;
            }

            // Add money to receiver
            PreparedStatement depositStatement =
                    connection.prepareStatement(depositSql);

            depositStatement.setDouble(1, amount);
            depositStatement.setLong(2, receiverAccount);

            int deposited = depositStatement.executeUpdate();

            if (deposited == 0) {

                connection.rollback();

                System.out.println(
                    "Transfer failed: receiver account not found!"
                );

                return;
            }

            // Record sender transaction
            PreparedStatement senderTransaction =
                    connection.prepareStatement(transactionSql);

            senderTransaction.setLong(1, senderAccount);
            senderTransaction.setString(2, "TRANSFER_OUT");
            senderTransaction.setDouble(3, amount);

            senderTransaction.executeUpdate();

            // Record receiver transaction
            PreparedStatement receiverTransaction =
                    connection.prepareStatement(transactionSql);

            receiverTransaction.setLong(1, receiverAccount);
            receiverTransaction.setString(2, "TRANSFER_IN");
            receiverTransaction.setDouble(3, amount);

            receiverTransaction.executeUpdate();

            // Save everything
            connection.commit();

            System.out.println("Transfer successful!");

        } catch (Exception e) {

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception rollbackError) {
                rollbackError.printStackTrace();
            }

            System.out.println(
                "Transfer failed. Transaction rolled back!"
            );

            e.printStackTrace();

        } finally {

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}