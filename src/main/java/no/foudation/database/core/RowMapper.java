package no.foudation.database.core;

import java.sql.SQLException;

/**
 * The {@code RowMapper} functional interface defines a contract for mapping one
 * or
 * more rows of a {@link MappedResult} to objects of a specific type {@code T}.
 * It
 * is intended
 * to be used when retrieving results from a database query.
 *
 * <p>
 * Implementing classes or lambda expressions that implement this functional
 * interface
 * must provide a logic to map one or more rows of the result to objects of
 * type {@code T}.
 * </p>
 * 
 * <p>
 * Note: The user implementing the RowMapper is responsible for calling the
 * {@code hasNext()}
 * method of the MappedResult and mapping the result to objects of the specified
 * type {@code T}.
 * The MappedResult received will always represent the final result, whether
 * it's a
 * query returning
 * rows and columns or a Data Manipulation Language (DML) operation returning
 * primary keys or rows affected.
 * Always make sure to type the class attributes correctly to avoid problems and
 * exceptions when calling {@code getColumn()} or
 * {@code getGeneratedKey} methods.
 * </p>
 * 
 * <p>
 * Tip: A good practice to use with PersistenceUnit is to implement methods that
 * already have the results mapped, so you can use the method reference in some
 * cases and make the work more automated.
 * </p>
 * 
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * public record Example(int id, String name) {
 * 
 *     public static Optional<Example> collectSingle(MappedResult result) {
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
 * @param <T> the specific type of objects to be mapped
 * @see MappedResult
 */
@FunctionalInterface
public interface RowMapper<T> {

    /**
     * Maps one or more rows of a {@link MappedResult} to objects of the specified
     * type
     * {@code T}.
     * The MappedResult will be automatically closed after use within the RowMapper.
     *
     * @param the result containing the data of one or more rows
     * @return the mapped objects of the specified type {@code T}
     * @throws SQLException if a database access error occurs or this method is
     *                      called on a closed result
     */
    T map(MappedResult map);
}
