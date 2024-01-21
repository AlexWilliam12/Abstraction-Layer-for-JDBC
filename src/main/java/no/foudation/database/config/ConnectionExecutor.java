package no.foudation.database.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The {@code ConnectionExecutor} functional interface defines a contract for an
 * operation
 * that can be executed with a {@link java.sql.Connection}. It is intended to be
 * used in
 * conjunction with the {@link ConnectionManager} for executing database
 * operations within
 * a managed connection context.
 *
 * <p>
 * Implementing classes or lambda expressions that implement this functional
 * interface
 * must provide an execution logic that takes a {@link java.sql.Connection} as a
 * parameter
 * and returns a result of type {@code T}.
 * </p>
 *
 * @param <T> the type of the result returned by the execution
 *
 * @see ConnectionManager
 */
@FunctionalInterface
public interface ConnectionExecutor<T> {

    /**
     * Executes an operation using the provided {@link java.sql.Connection}.
     *
     * @param connection the connection to be used for executing the operation
     * @return the result of the operation
     * @throws SQLException if a database access error occurs or the operation fails
     */
    T execute(Connection connection) throws SQLException;
}
