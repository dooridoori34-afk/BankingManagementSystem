package com.bank.dao;

import com.bank.model.Account;
import com.bank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AccountDAO {

    public void addAccount(Account account) {

        String sql = "INSERT INTO account " +
                     "(account_number, customer_id, account_type, balance) " +
                     "VALUES (?, ?, ?, ?)";

        try {
            Connection connection = DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setLong(1, account.getAccountNumber());
            statement.setInt(2, account.getCustomerId());
            statement.setString(3, account.getAccountType());
            statement.setDouble(4, account.getBalance());

            statement.executeUpdate();

            System.out.println("Account created successfully!");

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getBalance(long accountNumber) {

        String sql = "SELECT balance FROM account " +
                     "WHERE account_number = ?";

        try {
            Connection connection = DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setLong(1, accountNumber);

            var result = statement.executeQuery();

            if (result.next()) {

                double balance = result.getDouble("balance");

                connection.close();

                return balance;
            }

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}