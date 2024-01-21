package no.foudation.database.core;

/**
 * The {@code PersistenceBuilder} functional interface defines a contract for
 * building
 * persistence-related objects or entities based on a provided
 * {@link QueryBuilder}.
 *
 * <p>
 * Implementing classes or lambda expressions that implement this functional
 * interface
 * must provide a logic to build a persistence-related object using the
 * specified
 * {@link QueryBuilder}.
 * </p>
 * 
 * <p>
 * Example Usage:
 * </p>
 * 
 * <pre>
 * public final class ExampleService {
 * 
 *     private final PersistenceUnit persistenceUnit;
 * 
 *     public ExampleService(PersistenceUnit persistenceUnit) {
 *         this.persistenceUnit = persistenceUnit;
 *     }
 * 
 *     public List<Example> getList() {
 *         return persistenceUnit.persist(query -> query
 *                 .setQuery("SELECT * FROM example"))
 *                 .execute(Example::collectAll);
 *     }
 * 
 *     public Example getSingle(int id) {
 *         return persistenceUnit.persist(query -> query
 *                 .setQuery("SELECT * FROM example WHERE id = ?")
 *                 .setArgs(id))
 *                 .execute(Example::collectSingle)
 *                 .orElseThrow(() -> new NoSuchElementException());
 *     }
 * 
 *     public Example insert(Example example) {
 *         return persistenceUnit.persist(query -> query
 *                 .setQuery("INSERT INTO example VALUES (?, ?)")
 *                 .setArgs(example.getValues()))
 *                 .execute(map -> Example.collectInsertResult(map, example))
 *                 .orElseThrow(() -> new NoSuchElementException());
 *     }
 * 
 *     public Example update(Example example) {
 *         return persistenceUnit.persist(query -> query
 *                 .setQuery("UPDATE example SET name = ? WHERE id = ?")
 *                 .setArgs(example.name(), example.id()))
 *                 .execute(map -> Example.collectUpdateResult(map, example))
 *                 .get();
 *     }
 * 
 *     public boolean delete(int id) {
 *         return persistenceUnit.persist(query -> query
 *                 .setQuery("DELETE FROM example WHERE id = ?")
 *                 .setArgs(id))
 *                 .execute(Example::collectDeleteResult)
 *                 .get();
 *     }
 * }
 * 
 * public record Example(int id, String name) {
 * 
 *     public static Optional^Example> collectSingle(MappedResult result) {
 *         return result.hasNext()
 *                 ? Optional.ofNullable(
 *                         new Example(
 *                                 result.getColumn("id"),
 *                                 result.getColumn("name")))
 *                 : Optional.ofNullable(null);
 *     }
 * 
 *     public static List<Example> collectAll(MappedResult result) {
 *         List<Example> examples = new ArrayList<>();
 *         while (result.hasNext()) {
 *             examples.add(
 *                     new Example(
 *                             result.getColumn("id"),
 *                             result.getColumn("name")));
 *         }
 *         return examples;
 *     }
 * 
 *     public static Optional<Example> collectInsertResult(MappedResult result, Example example) {
 *         return result.hasNext()
 *                 ? Optional.ofNullable(
 *                         new Example(
 *                                 result.getGeneratedKey(),
 *                                 example.name))
 *                 : Optional.ofNullable(null);
 *     }
 * 
 *     public static Supplier<Example> collectUpdateResult(MappedResult result, Example example) {
 *         return () -> {
 *             if (result.getRowsAffected() == 0) {
 *                 throw new IllegalArgumentException();
 *             }
 *             return example;
 *         };
 *     }
 * 
 *     public static Supplier<Boolean> collectDeleteResult(MappedResult result) {
 *         return () -> result.getRowsAffected() > 0;
 *     }
 * 
 *     public Object[] getValues() {
 *         return new Object[] { id, name };
 *     }
 * }
 * </pre>
 * 
 * @param <T> the type of the result object to be built
 *
 * @see QueryBuilder
 */
@FunctionalInterface
public interface PersistenceBuilder<T> {

    /**
     * Builds a persistence-related object using the provided {@link QueryBuilder}.
     *
     * @param builder the query builder used to construct the persistence-related
     *                object
     * @return the built persistence-related object
     */
    T build(QueryBuilder builder);
}
