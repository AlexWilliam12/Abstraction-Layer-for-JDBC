package no.foudation.database.core;

/**
 * The {@code QueryStatement} functional interface defines a contract for
 * constructing
 * a {@link QueryBuilder} within the context of a query statement. It is
 * intended to be
 * used for building query strings and associated query arguments.
 *
 * <p>
 * Implementing classes or lambda expressions that implement this functional
 * interface
 * must provide a logic to build a {@link QueryBuilder} based on the provided
 * {@link QueryBuilder} instance.
 * </p>
 *
 * @see QueryBuilder
 */
@FunctionalInterface
public interface QueryStatement {

    /**
     * Constructs a {@link QueryBuilder} within the context of a query statement.
     *
     * @param query the query builder to be used for constructing the query
     * @return the constructed query builder
     */
    QueryBuilder build(QueryBuilder query);
}
