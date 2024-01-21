package no.foudation.database.config;

/**
 * The {@code ConnectionLoader} interface defines a contract for providing
 * information
 * required to establish a connection to a data source. Implementing classes or
 * objects
 * that implement this interface must provide methods to retrieve the driver
 * class name,
 * database URL, username, and password.
 *
 * <p>
 * This interface is typically used in conjunction with a
 * {@link ConnectionManager} to obtain
 * the necessary details for establishing a connection to a database or other
 * data source.
 * </p>
 *
 * <p>
 * Example Usage:
 * </p>
 * 
 * <pre>
 * // Implemented class
 * public final class ConnectionLoaderImplemented implements ConnectionLoader {
 * 
 *     private String url;
 *     private String username;
 *     private String password;
 *     private String driver;
 * 
 *     public ConnectionLoaderImplemented(String path) {
 *         loader(Path.of(path));
 *     }
 * 
 *     private void loader(Path path) {
 *         try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
 *             String line;
 *             while ((line = reader.readLine()) != null) {
 *                 if (line.contains("DATABASE_URL")) {
 *                     url = getProperty(line);
 *                 } else if (line.contains("DATABASE_USERNAME")) {
 *                     username = getProperty(line);
 *                 } else if (line.contains("DATABASE_PASSWORD")) {
 *                     password = getProperty(line);
 *                 } else if (line.contains("DATABASE_DRIVER")) {
 *                     driver = getProperty(line);
 *                 }
 *             }
 *         } catch (IOException e) {
 *             throw new RuntimeException(e);
 *         }
 *     }
 * 
 *     private String getProperty(String property) {
 *         return property.substring(property.lastIndexOf("=") + 1);
 *     }
 * 
 *     &#64;Override
 *     public String getDriver() {
 *         return driver;
 *     }
 * 
 *     &#64;Override
 *     public String getUrl() {
 *         return url;
 *     }
 * 
 *     &#64;Override
 *     public String getUsername() {
 *         return username;
 *     }
 * 
 *     &#64;Override
 *     public String getPassword() {
 *         return password;
 *     }
 * }
 * </pre>
 *
 * @see ConnectionManager
 */
public interface ConnectionLoader {

    /**
     * Retrieves the driver class name required to establish a connection.
     *
     * @return the driver class name
     */
    String getDriver();

    /**
     * Retrieves the database URL required to establish a connection.
     *
     * @return the database URL
     */
    String getUrl();

    /**
     * Retrieves the username required to establish a connection.
     *
     * @return the username
     */
    String getUsername();

    /**
     * Retrieves the password required to establish a connection.
     *
     * @return the password
     */
    String getPassword();
}
