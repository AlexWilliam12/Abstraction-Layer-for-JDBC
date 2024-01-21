package no.foudation.database.utils;

/**
 * The {@code PersistenceException} class represents an exception that is
 * specifically
 * related to persistence operations. It extends the {@code RuntimeException}
 * class,
 * making it an unchecked exception.
 *
 * <p>
 * Instances of this exception can be thrown to indicate errors or exceptional
 * conditions
 * during persistence-related operations. This class provides constructors for
 * creating
 * instances with a custom error message and an optional cause (Throwable).
 * </p>
 *
 * <p>
 * Example Usage:
 * </p>
 * 
 * <pre>
 * try {
 *     // Perform persistence operation
 * } catch (PersistenceException e) {
 *     // Handle persistence-related exception
 *     System.err.println("Persistence error: " + e.getMessage());
 *     e.printStackTrace();
 * }
 * </pre>
 *
 * @see RuntimeException
 */
public final class PersistenceException extends RuntimeException {

    /**
     * Constructs a new {@code PersistenceException} with the specified detail
     * message
     * and the given throwable as the cause.
     *
     * @param message   the detail message (which is saved for later retrieval by
     *                  the {@link #getMessage()} method)
     * @param throwable the cause (which is saved for later retrieval by the
     *                  {@link #getCause()} method)
     */
    public PersistenceException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new {@code PersistenceException} with the specified detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method)
     */
    public PersistenceException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code PersistenceException} with the specified throwable as
     * the cause.
     *
     * @param throwable the cause (which is saved for later retrieval by the
     *                  {@link #getCause()} method)
     */
    public PersistenceException(Throwable throwable) {
        super(throwable);
    }
}
