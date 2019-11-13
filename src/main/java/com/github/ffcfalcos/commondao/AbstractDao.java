package com.github.ffcfalcos.commondao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess","unused","Duplicates"})
public abstract class AbstractDao<T> implements AbstractDaoInterface<T> {

    protected void insert(String query, Object[] parameters) throws Exception {
        PreparedStatement preparedStatement = this.generateStatement(query, parameters);
        preparedStatement.execute();
    }

    protected void update(String query, Object[] parameters) throws Exception {
        PreparedStatement preparedStatement = this.generateStatement(query, parameters);
        preparedStatement.executeUpdate();
    }

    protected T getById(String query, int id) throws Exception {
        PreparedStatement preparedStatement = this.getConnection().prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return generateEntity(resultSet);
    }

    protected T getOne(String query, Object[] parameters) throws Exception {
        PreparedStatement preparedStatement = this.generateStatement(query, parameters);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return generateEntity(resultSet);
    }

    protected List<T> getMultiple(String query, Object[] parameters) throws Exception {
        List<T> list = new ArrayList<>();
        PreparedStatement preparedStatement = this.generateStatement(query, parameters);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()) {
            list.add(generateEntity(resultSet));
        }
        return list;
    }

    protected List<T> getAll(String query) throws Exception {
        List<T> list = new ArrayList<>();
        PreparedStatement preparedStatement = this.getConnection().prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()) {
            list.add(generateEntity(resultSet));
        }
        return list;
    }

    protected ResultSet get(String query, Object[] parameters) throws Exception {
        PreparedStatement preparedStatement = this.generateStatement(query, parameters);
        return preparedStatement.executeQuery();
    }

    protected void deleteById(String query, int id) throws Exception {
        PreparedStatement preparedStatement = this.getConnection().prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.execute();
    }

    protected void delete(String query, Object[] parameters) throws Exception {
        insert(query, parameters);
    }

    protected abstract Connection getConnection() throws Exception;

    protected abstract T generateEntity(ResultSet resultSet) throws SQLException;

    private PreparedStatement generateStatement(String query, Object[] parameters) throws Exception {
        PreparedStatement preparedStatement = this.getConnection().prepareStatement(query);
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            if (parameter.getClass() == Double.class) {
                preparedStatement.setDouble(i + 1, (double) parameter);
                continue;
            }
            if (parameter.getClass() == Integer.class) {
                preparedStatement.setInt(i + 1, (int) parameter);
                continue;
            }
            if (parameter.getClass() == String.class) {
                preparedStatement.setString(i + 1, (String) parameter);
                continue;
            }
            if (parameter.getClass() == Boolean.class) {
                preparedStatement.setBoolean(i + 1, (boolean) parameter);
                continue;
            }
            if (parameter.getClass() == Long.class) {
                preparedStatement.setLong(i + 1, (long) parameter);
            }
        }
        return preparedStatement;
    }

}
