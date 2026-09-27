package com.bank.dao;

import com.bank.model.Transaction;
import com.bank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TransactionDAO {

    public void addTransaction(Transaction transaction) {

        String sql = "INSERT INTO transaction_history " +
                     "(account_number, transaction_type, amount) " +
                     "VALUES (?, ?, ?)";

        try {
            Connection connection = DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setLong(1, transaction.getAccountNumber());
            statement.setString(2, transaction.getTransactionType());
            statement.setDouble(3, transaction.getAmount());

            statement.executeUpdate();

            System.out.println("Transaction recorded successfully!");

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Display transaction history
    public void getTransactions(long accountNumber) {

        String sql = "SELECT transaction_id, transaction_type, " +
                     "amount, transaction_date " +
                     "FROM transaction_history " +
                     "WHERE account_number = ? " +
                     "ORDER BY transaction_date DESC";

        try {
            Connection connection = DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setLong(1, accountNumber);

            ResultSet result = statement.executeQuery();

            System.out.println("\n===== TRANSACTION HISTORY =====");

            boolean found = false;

            while (result.next()) {

                found = true;

                System.out.println(
                    "ID: " + result.getInt("transaction_id") +
                    " | Type: " + result.getString("transaction_type") +
                    " | Amount: Rs." + result.getDouble("amount") +
                    " | Date: " + result.getTimestamp("transaction_date")
                );
            }

            if (!found) {
                System.out.println("No transactions found!");
            }

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}