package com.github.ffcfalcos.commondao;

import com.github.ffcfalcos.logformatter.LogDaoFormatter;
import com.github.ffcfalcos.logformatter.LogDaoFormatterInterface;
import com.github.ffcfalcos.logformatter.LogType;
import com.github.ffcfalcos.logger.Logger;
import com.github.ffcfalcos.logger.LoggerInterface;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class offer multiple generic SQL commands to easily execute hard SQL query
 * If you want to edit the logger, you have to do it on the class extending this one
 * If you are using Beans, create a @PostConstructed method to set Parameters
 * Advice: Create an abstract class where you declare the getConnection method for all Dao of the same database
 * @param <T> The entity manage with the table
 */
@SuppressWarnings("WeakerAccess")
public abstract class CommonDao<T> {

    protected final LoggerInterface logger = new Logger();
    protected final LogDaoFormatterInterface logDaoFormatter = new LogDaoFormatter();

    /**
     * Execute an Insert Query with the SQL query and his parameters
     * @param query String
     * @param parameters Object[]
     *                   Parameters must be indexed with the same of order as the SQL Query
     */
    protected void insert(String query, Object[] parameters) {
        List<JSONObject> logContent = logDaoFormatter.init(LogType.DAO);
        try {
            PreparedStatement preparedStatement = this.generateStatement(query, parameters);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            preparedStatement.execute();
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
        }
        logger.log(logDaoFormatter.close(logContent));
    }

    /**
     * Execute an Update Query with the SQL query and his parameters
     * @param query String
     * @param parameters Object[]
     *                   Parameters must be indexed with the same of order as the SQL Query
     */
    protected void update(String query, Object[] parameters) {
        List<JSONObject> logContent = logDaoFormatter.init(LogType.DAO);
        try {
            PreparedStatement preparedStatement = this.generateStatement(query, parameters);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
        }
        logger.log(logDaoFormatter.close(logContent));
    }

    /**
     * Execute a Select Query to get an Entity by his id with the SQL query and his id
     * @param query String
     * @param id int
     * @return T
     */
    protected T getById(String query, int id) {
        List<JSONObject> logContent = logDaoFormatter.init(LogType.DAO);
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, id);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            T result = generateEntity(resultSet);
            logDaoFormatter.addResponse(logContent, result);
            return result;
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
            logger.log(logDaoFormatter.close(logContent));
            return null;
        }
    }

    /**
     * Execute a Select Query to get a Result with one Entity with the SQL query and his parameters
     * @param query String
     * @param parameters Object[]
     *                   Parameters must be indexed with the same of order as the SQL Query
     * @return T
     */
    protected T getOne(String query, Object[] parameters) {
        List<JSONObject> logContent = logDaoFormatter.init(LogType.DAO);
        try {
            PreparedStatement preparedStatement = this.generateStatement(query, parameters);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            T result = generateEntity(resultSet);
            logDaoFormatter.addResponse(logContent, result);
            return result;
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
            logger.log(logDaoFormatter.close(logContent));
            return null;
        }
    }

    /**
     * Execute a Select Query to get a Result with multiple Entities with the SQL query and his parameters
     * @param query String
     * @param parameters Object[]
     *                   Parameters must be indexed with the same of order as the SQL Query
     * @return T[]
     */
    protected List<T> getMultiple(String query, Object[] parameters) {
        List<JSONObject> logContent = logDaoFormatter.init(LogType.DAO);
        List<T> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = this.generateStatement(query, parameters);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                list.add(generateEntity(resultSet));
            }
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
        }
        logDaoFormatter.addResponse(logContent, list);
        return list;
    }

    /**
     * Execute a Select Query with the SQL query
     * @param query String
     * @return T[]
     */
    protected List<T> getAll(String query) {
        List<JSONObject> logContent = logDaoFormatter.init(LogType.DAO);
        List<T> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement(query);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                list.add(generateEntity(resultSet));
            }
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
        }
        logDaoFormatter.addResponse(logContent, list);
        return list;
    }

    /**
     * Execute a Delete Query with the SQL query and the id
     * @param query String
     * @param id int
     */
    protected void deleteById(String query, int id) {
        List<JSONObject> logContent = logDaoFormatter.init(LogType.DAO);
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, id);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            preparedStatement.execute();
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
        }
        logger.log(logDaoFormatter.close(logContent));
    }

    /**
     * Execute a Delete Query with the SQL query and his parameters
     * @param query String
     * @param parameters Object[]
     *                   Parameters must be indexed with the same of order as the SQL Query
     */
    protected void delete(String query, Object[] parameters) {
        insert(query, parameters);
    }

    /**
     * Create a SQL Connection which must stay open
     * Make sur that this method doesn't throw any Exception cause it may be catch and hidden
     * Dont forget to load the Driver
     * @return Connection a SQL Connection
     */
    protected abstract Connection getConnection();

    /**
     * Generate an Entity T with the resultSet
     * @param resultSet ResultSet
     * @return T
     * @throws SQLException if an index of resultSet doesn't exists
     */
    protected abstract T generateEntity(ResultSet resultSet) throws SQLException;

    /**
     * Generate a Statement with an array parameters and a SQL Query
     * @param query String the SQL Query
     * @param parameters Object[]
     * @return PreparedStatement
     * @throws SQLException if there is an error with the connexion
     */
    private PreparedStatement generateStatement(String query, Object[] parameters) throws SQLException {
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
