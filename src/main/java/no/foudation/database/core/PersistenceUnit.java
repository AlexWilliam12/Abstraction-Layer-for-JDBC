package no.foudation.database.core;

import no.foudation.database.config.ConnectionManager;
import no.foudation.database.utils.PersistenceException;
import no.foudation.database.utils.PersistenceValidator;

/**
 * The {@code PersistenceUnit} class represents a unit of persistence within an
 * application.
 * It encapsulates a {@link ConnectionManager} and provides a method to build
 * queries using
 * a specified {@link QueryStatement}.
 *
 * <p>
 * Example Usage:
 * </p>
 * 
 * <pre>
 * public class Main {
 *     public static void main(String[] args) {
 * 
 *         ConnectionLoader loader = new ConnectionLoaderImplemented();
 *         ConnectionManager manager = new ConnectionManager(loader);
 *         PersistenceUnit persistenceUnit = new PersistenceUnit(manager);
 * 
 *         Entity entity = persistenceUnit.persist(query -> query
 *                 .setQuery("SELECT * FROM table_name WHERE id = ?")
 *                 .setArgs(1))
 *                 .execute(set -> new Entity(set.getObject("column_name")));
 *     }
 * }
 * </pre>
 *
 * @see ConnectionManager
 * @see QueryStatement
 * @see QueryCollector
 * @see PersistenceValidator
 */
public final class PersistenceUnit {

    private final ConnectionManager manager;

    /**
     * Constructs a new {@code PersistenceUnit} with the specified
     * {@link ConnectionManager}.
     *
     * @param manager the connection manager used for managing database connections
     * @throws PersistenceException if the specified manager is null
     */
    public PersistenceUnit(ConnectionManager manager) {
        this.manager = PersistenceValidator.requireNonNull(manager, "ConnectionManager manager");
    }

    /**
     * Persists data using the provided {@link QueryStatement} within the managed
     * connection context.
     *
     * @param query the query statement representing the database operation
     * @return a {@link QueryCollector} for collecting results of the executed query
     * @throws PersistenceException if the specified query is null
     */
    public QueryCollector persist(QueryStatement query) {
        return new QueryCollector(manager, PersistenceValidator
                .requireNonNull(query, "QueryStatement query")
                .build(new QueryBuilder()));
    }
}
