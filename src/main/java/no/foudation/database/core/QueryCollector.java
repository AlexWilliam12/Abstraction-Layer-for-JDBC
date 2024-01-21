package no.foudation.database.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import no.foudation.database.config.ConnectionManager;
import no.foudation.database.utils.PersistenceException;
import no.foudation.database.utils.PersistenceValidator;

/**
 * The {@code QueryCollector} class provides a mechanism for executing SQL
 * queries
 * and collecting the results using a {@link RowMapper}. It is part of a
 * higher-level
 * persistence framework and works in conjunction with the
 * {@link ConnectionManager}.
 *
 * <p>
 * Note: This class is responsible for executing SQL queries and handling the
 * result set
 * based on the type of query (SELECT, INSERT, UPDATE, DELETE). The result is
 * then
 * mapped using the provided {@link RowMapper}.
 * </p>
 *
 * @see ConnectionManager
 * @see RowMapper
 * @see QueryBuilder
 */
public class QueryCollector {

    private final ConnectionManager manager;
    private final Logger logger;
    private final String query;
    private Object[] args;

    /**
     * Constructs a {@code QueryCollector} with the specified
     * {@link ConnectionManager}
     * and {@link QueryBuilder}.
     *
     * @param manager the connection manager
     * @param query   the query builder containing the SQL query and arguments
     * @throws PersistenceException if the query or connection manager is null
     */
    QueryCollector(ConnectionManager manager, QueryBuilder query) {
        this.manager = PersistenceValidator.requireNonNull(manager, "ConnectionManager manager");
        this.logger = manager.getLogger();
        this.query = PersistenceValidator.requireNonNull(query.getQuery(), "String query");
        this.args = query.getArgs();
    }

    /**
     * Executes the SQL query and maps the result using the provided
     * {@link RowMapper}.
     *
     * @param <T>    the type of the result
     * @param mapper the row mapper to map the result set to an object
     * @return the result of executing the query, mapped to the specified type
     * @throws PersistenceException if an error occurs during query execution
     */
    public <T> T execute(RowMapper<T> mapper) {
        return PersistenceValidator.hasArguments(args)
                ? executeWithArgs(PersistenceValidator
                        .requireNonNull(mapper, "RowMapper<T> mapper"))
                : executeWithoutArgs(PersistenceValidator
                        .requireNonNull(mapper, "RowMapper<T> mapper"));
    }

    private <T> T executeWithArgs(RowMapper<T> mapper) {
        return manager.execute(connection -> {
            logger.info("The query statement has been built\nQuery: " + query);
            try (PreparedStatement statement = connection.prepareStatement(query,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                prepareStatementArgs(statement);
                return getMappedResult(connection, statement, mapper);
            } catch (SQLException e) {
                connection.rollback();
                throw new PersistenceException("The execution of the query statement has failed", e);
            }
        });
    }

    private <T> T executeWithoutArgs(RowMapper<T> mapper) {
        return manager.execute(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(query,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                return getMappedResult(connection, statement, mapper);
            } catch (SQLException e) {
                connection.rollback();
                throw new PersistenceException("The execution of the query statement has failed", e);
            }
        });
    }

    private void prepareStatementArgs(PreparedStatement statement) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            statement.setObject(i + 1, args[i]);
        }
    }

    private <T> T getMappedResult(Connection connection, PreparedStatement statement, RowMapper<T> mapper)
            throws SQLException {
        if (!statement.execute()) {
            connection.commit();
            if (query.contains("INSERT")) {
                return getInsertResult(statement.getGeneratedKeys(), mapper, new MappedResult());
            } else if (query.contains("UPDATE")) {
                return getUpdateResult(statement.getUpdateCount(), mapper, new MappedResult());
            } else if (query.contains("DELETE")) {
                return getDeleteResult(statement.getUpdateCount(), mapper, new MappedResult());
            }
        }
        connection.commit();
        return getSelectResult(statement.getResultSet(), mapper, new MappedResult());
    }

    private <T> T getSelectResult(ResultSet set, RowMapper<T> mapper, MappedResult result) throws SQLException {
        try (set) {
            List<Map<String, Object>> maps = new ArrayList<>();
            ResultSetMetaData metadata = set.getMetaData();
            int columnCount = metadata.getColumnCount();
            while (set.next()) {
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    map.put(metadata.getColumnName(i), set.getObject(i));
                }
                maps.add(map);
            }
            result.setMappedResult(maps);
            return mapper.map(result);
        }
    }

    private <T> T getInsertResult(ResultSet set, RowMapper<T> mapper, MappedResult result) throws SQLException {
        try (set) {
            List<Object> list = new ArrayList<>();
            while (set.next()) {
                list.add(set.getObject(1));
            }
            result.setGeneratedKeys(list);
            return mapper.map(result);
        }
    }

    private <T> T getUpdateResult(int rowsAffected, RowMapper<T> mapper, MappedResult result) {
        result.setRowsAffected(rowsAffected);
        return mapper.map(result);
    }

    private <T> T getDeleteResult(int rowsAffected, RowMapper<T> mapper, MappedResult result) {
        result.setRowsAffected(rowsAffected);
        return mapper.map(result);
    }
}
