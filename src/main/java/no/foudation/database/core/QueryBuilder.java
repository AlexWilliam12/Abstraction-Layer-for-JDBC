package no.foudation.database.core;

import no.foudation.database.utils.PersistenceValidator;

/**
 * The {@code QueryBuilder} class is responsible for constructing a query string
 * and
 * managing the associated query arguments. It provides methods to set the query
 * string
 * and the query arguments, and also exposes methods to retrieve these values.
 *
 * @see PersistenceValidator
 */
public final class QueryBuilder {

    private String query;

    private Object[] args;

    /**
     * Sets the query string for the {@code QueryBuilder}.
     *
     * @param query the query string to be set
     * @return this {@code QueryBuilder} instance for method chaining
     * @throws PersistenceException if the specified query is null
     */
    public QueryBuilder setQuery(String query) {
        this.query = PersistenceValidator.requireNonNull(query, "String query");
        return this;
    }

    /**
     * Sets the query arguments for the {@code QueryBuilder}.
     *
     * @param args the query arguments to be set
     * @return this {@code QueryBuilder} instance for method chaining
     * @throws PersistenceException if the specified array of arguments is null or
     *                              empty
     */
    public QueryBuilder setArgs(Object... args) {
        this.args = PersistenceValidator.requireLeastOneArgument(args, "Object... args");
        return this;
    }

    /**
     * Gets the current query string set in the {@code QueryBuilder}.
     *
     * @return the current query string
     */
    String getQuery() {
        return query;
    }

    /**
     * Gets the current query arguments set in the {@code QueryBuilder}.
     *
     * @return the current query arguments as an array
     */
    Object[] getArgs() {
        return args;
    }
}
