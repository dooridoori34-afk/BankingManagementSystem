package com.bank.dao;

import com.bank.model.Customer;
import com.bank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class CustomerDAO {

    public void addCustomer(
            Customer customer,
            String password) {

        String sql =
                "INSERT INTO customer " +
                "(name, phone, email, password) " +
                "VALUES (?, ?, ?, ?)";

        try {

            Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, customer.getName());
            statement.setString(2, customer.getPhone());
            statement.setString(3, customer.getEmail());
            statement.setString(4, password);

            statement.executeUpdate();

            System.out.println(
                    "Customer added successfully!"
            );

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}