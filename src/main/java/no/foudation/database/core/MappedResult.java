package no.foudation.database.core;

import java.util.List;
import java.util.Map;

import no.foudation.database.utils.PersistenceException;

/**
 * The {@code MappedResult} class represents the result of a database query
 * execution,
 * including the mapped results for SELECT queries, generated keys for INSERT
 * queries,
 * and the number of affected rows for UPDATE and DELETE queries. It is used by
 * the
 * {@link QueryCollector} to provide a convenient interface for accessing query
 * results.
 * 
 * <p>
 * Note: Users should follow the appropriate method calls based on the type of
 * query being executed.
 * For example, use {@link #setMappedResult(List)} for SELECT queries,
 * {@link #setGeneratedKeys(List)}
 * for INSERT queries, and {@link #setRowsAffected(int)} for UPDATE and DELETE
 * queries.
 * The class provides methods like {@link #hasNext()},
 * {@link #getColumn(String)}, {@link #getGeneratedKey()},
 * and {@link #getRowsAffected()} to access the query results based on the query
 * type.
 * </p>
 *
 * @see QueryCollector
 * @see RowMapper
 */
public final class MappedResult {

    private List<Map<String, Object>> mappedResult;
    private List<Object> generatedKeys;
    private int index;
    private int rowsAffected;

    /**
     * Constructs an empty {@code MappedResult}.
     */
    MappedResult() {
    }

    /**
     * Sets the mapped result for SELECT queries.
     *
     * @param mappedResult the list of mapped results containing column names and
     *                     values
     */
    void setMappedResult(List<Map<String, Object>> mappedResult) {
        this.mappedResult = mappedResult;
        this.index = -1;
        this.rowsAffected = -1;
    }

    /**
     * Sets the generated keys for INSERT queries.
     *
     * @param generatedKeys the list of generated keys
     */
    void setGeneratedKeys(List<Object> generatedKeys) {
        this.generatedKeys = generatedKeys;
        this.index = -1;
        this.rowsAffected = -1;
    }

    /**
     * Sets the number of affected rows for UPDATE and DELETE queries.
     *
     * @param rowsAffected the number of rows affected by the query
     */
    void setRowsAffected(int rowsAffected) {
        this.rowsAffected = rowsAffected;
    }

    /**
     * Checks if there are more results available.
     *
     * @return {@code true} if there are more results, {@code false} otherwise
     * @throws PersistenceException if no query results are available or if called
     *                              out of scope
     */
    public boolean hasNext() {
        if (mappedResult == null && generatedKeys == null) {
            throw new PersistenceException("There are no query results (Avoid calling methods that are out of scope)");
        }
        index++;
        return mappedResult != null
                ? index < mappedResult.size()
                : index < generatedKeys.size();
    }

    /**
     * Gets the value of the specified column for the current result row.
     *
     * @param columnName the name of the column
     * @param <T>        the type of the column value
     * @return the value of the specified column
     * @throws PersistenceException if no results with named columns are available
     *                              or if called out of scope
     */
    @SuppressWarnings("unchecked")
    public <T> T getColumn(String columnName) {
        if (mappedResult == null) {
            throw new PersistenceException(
                    "There are no results with named columns (Avoid calling methods that are out of scope)");
        }
        if (index >= 0 && index < mappedResult.size()) {
            return (T) mappedResult.get(index).get(columnName);
        } else {
            throw new PersistenceException("You need to call the hasNext() method to move forward in MappedResult");
        }
    }

    /**
     * Gets the generated key for the current result row.
     *
     * @param <T> the type of the generated key
     * @return the generated key for the current result row
     * @throws PersistenceException if no generated keys are available or if called
     *                              out of scope
     */
    @SuppressWarnings("unchecked")
    public <T> T getGeneratedKey() {
        if (generatedKeys == null) {
            throw new PersistenceException(
                    "There is no key generated per affected line (Avoid calling methods that are out of scope)");
        }
        if (index >= 0 && index < generatedKeys.size()) {
            return (T) generatedKeys.get(index);
        } else {
            throw new PersistenceException("You need to call the hasNext() method to move forward in MappedResult");
        }
    }

    /**
     * Gets the number of rows affected by the query.
     *
     * @return the number of rows affected
     * @throws PersistenceException if no execution thread was affected or if called
     *                              out of scope
     */
    public int getRowsAffected() {
        if (rowsAffected == -1) {
            throw new PersistenceException(
                    "No execution thread was affected (Avoid calling methods that are out of scope)");
        }
        return rowsAffected;
    }
}
