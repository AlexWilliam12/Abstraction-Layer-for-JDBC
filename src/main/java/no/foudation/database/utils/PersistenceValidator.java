package no.foudation.database.utils;

/**
 * The {@code PersistenceValidator} class provides utility methods for
 * validating
 * persistence-related operations, ensuring that required parameters are
 * non-null
 * and that certain conditions are met.
 */
public final class PersistenceValidator {

    /**
     * Checks if the specified object is non-null and throws a
     * {@code PersistenceException}
     * if it is null, providing a descriptive error message including the parameter
     * name.
     *
     * @param <T>  the type of the object
     * @param obj  the object to be checked for null
     * @param name the name of the parameter being checked
     * @return the non-null object if it is not null
     * @throws PersistenceException if the specified object is null
     */
    public static <T> T requireNonNull(T obj, String name) {
        if (obj == null) {
            throw new PersistenceException("The '" + name + "' parameter has not been initialized");
        }
        return obj;
    }

    /**
     * Checks if the specified array of arguments is non-null and has at least one
     * element.
     * Throws a {@code PersistenceException} if the array is null or empty,
     * providing
     * a descriptive error message including the parameter name.
     *
     * @param args the array of arguments to be checked
     * @param name the name of the parameter being checked
     * @return the non-null and non-empty array of arguments
     * @throws PersistenceException if the specified array is null or empty
     */
    public static Object[] requireLeastOneArgument(Object[] args, String name) {
        if (requireNonNull(args, "args").length <= 0) {
            throw new PersistenceException("The '" + name + "' parameter must have at least one argument");
        }
        return args;
    }

    /**
     * Checks if the specified array of arguments is non-null and has at least one
     * element.
     *
     * @param args the array of arguments to be checked
     * @return {@code true} if the array is non-null and has at least one element,
     *         {@code false} otherwise
     */
    public static boolean hasArguments(Object[] args) {
        return args != null && args.length > 0;
    }
}
