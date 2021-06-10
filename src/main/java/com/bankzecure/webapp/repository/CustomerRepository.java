package com.bankzecure.webapp.repository;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.bankzecure.webapp.entity.*;
import com.bankzecure.webapp.JdbcUtils;

public class CustomerRepository {
  private final static String DB_URL = "jdbc:mysql://localhost:3306/springboot_bankzecure?serverTimezone=GMT";
	private final static String DB_USERNAME = "bankzecure";
	private final static String DB_PASSWORD = "Ultr4B4nk@L0nd0n";

  public Customer findByIdentifierAndPassword(final String identifier, final String password) {
    Connection connection = null;
    // Statement statement = null;
    PreparedStatement prepState = null;
    ResultSet resultSet = null;
    try {
      connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
      // statement = connection.createStatement();
     final String query = "SELECT * FROM customer WHERE identifier=? AND password=?";
      // final String query = "SELECT * FROM customer " +
      //  "WHERE identifier = '" + identifier + "' AND password = '" + password + "'";
      prepState = connection.prepareStatement(query);
      prepState.setString(1, identifier);
      prepState.setString(2, password);
      // resultSet = statement.executeQuery(query);
      System.out.println(prepState);
      resultSet = prepState.executeQuery();
      
      Customer customer = null;

      if (resultSet.next()) {
          System.out.println("resultSet non vide");
        final int id = resultSet.getInt("id");
        final String identifierInDb = resultSet.getString("identifier");
        final String firstName = resultSet.getString("first_name");
        final String lastName = resultSet.getString("last_name");
        final String email = resultSet.getString("email");
        customer = new Customer(id, identifierInDb, firstName, lastName, email);
      } else {
          System.out.println("resultSet vide");
      }
      return customer;
    } catch (final SQLException e) {
      e.printStackTrace();
    } finally {
      JdbcUtils.closeResultSet(resultSet);
      JdbcUtils.closeStatement(prepState);
      JdbcUtils.closeConnection(connection);
    }
    return null;
  }

  public Customer update(String identifier, String newEmail, String newPassword) {

    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    Customer customer = null;
    try {
        // Connection and statement
        connection = DriverManager.getConnection(
          DB_URL, DB_USERNAME, DB_PASSWORD
        );
      //  statement = connection.createStatement();

        // Build the update query using a QueryBuilder
        StringBuilder queryBuilder = new StringBuilder();
      //  queryBuilder.append("UPDATE customer SET email = '" + newEmail + "'");
        queryBuilder.append("UPDATE customer SET email = ?");
        // Don't set the password in the update query, if it's not provided
        if (newPassword != "") {
          // queryBuilder.append(",password = '" + newPassword + "'");
            queryBuilder.append(",password = ?");
        }
        //queryBuilder.append(" WHERE identifier = '" + identifier + "'");
        queryBuilder.append(" WHERE identifier = ?");
        String query = queryBuilder.toString();
        statement = connection.prepareStatement(query);
        statement.setString(1, newEmail);
        if (newPassword != "") {
            statement.setString(2, newPassword);
            statement.setString(3, identifier);
        } else {
            statement.setString(2, identifier);
        }
        System.out.println(statement);
        statement.executeUpdate();

        JdbcUtils.closeStatement(statement);
        JdbcUtils.closeConnection(connection);

        connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        // statement = connection.createStatement();
        // query = "SELECT * FROM customer WHERE identifier = '" + identifier + "'";
        query = "SELECT * FROM customer WHERE identifier = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, identifier);
        System.out.println(statement);
        resultSet = statement.executeQuery();
        if (resultSet.next()) {
          final int id = resultSet.getInt("id");
          final String identifierInDb = resultSet.getString("identifier");
          final String firstName = resultSet.getString("first_name");
          final String lastName = resultSet.getString("last_name");
          final String email = resultSet.getString("email");
          customer = new Customer(id, identifierInDb, firstName, lastName, email);
        }
        return customer;
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
   	    JdbcUtils.closeStatement(statement);
   	    JdbcUtils.closeConnection(connection);
    }
    return null;
}
}