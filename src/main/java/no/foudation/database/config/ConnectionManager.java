package no.foudation.database.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.foudation.database.utils.PersistenceException;
import no.foudation.database.utils.PersistenceValidator;

/**
 * The {@code ConnectionManager} class provides a convenient and automated way
 * to manage
 * database connections within a persistence context. It works in conjunction
 * with a
 * {@link ConnectionLoader} to obtain necessary connection details and a
 * {@link ConnectionExecutor}
 * to execute operations within the managed connection context.
 *
 * <p>
 * The class supports transaction management, logging, and exception handling
 * for database operations.
 * </p>
 *
 * @see ConnectionLoader
 * @see ConnectionExecutor
 * @see PersistenceValidator
 */
public final class ConnectionManager {

    private final Logger logger = Logger.getLogger(ConnectionManager.class.getName());

    private final ConnectionLoader loader;

    /**
     * Constructs a new {@code ConnectionManager} with the specified
     * {@link ConnectionLoader}.
     *
     * @param loader the connection loader providing connection details
     * @throws PersistenceException if the specified loader is null
     */
    public ConnectionManager(ConnectionLoader loader) {
        this.loader = PersistenceValidator.requireNonNull(loader, "ConnectionLoader loader");
    }

    /**
     * Executes a database operation within a managed connection context.
     *
     * @param <T>      the type of the result returned by the execution
     * @param executor the connection executor representing the database operation
     * @return the result of the database operation
     * @throws PersistenceException if a database access error occurs during
     *                              execution
     */
    public <T> T execute(ConnectionExecutor<T> executor) {
        try {
            // Load the JDBC driver class
            Class.forName(PersistenceValidator.requireNonNull(loader.getDriver(), "driver"));
            // Obtain a connection and set auto-commit to false
            try (Connection connection = getConnection()) {
                logger.info("The database connection has been accepted");
                connection.setAutoCommit(false);
                // Execute the provided operation within the managed connection context
                return PersistenceValidator
                        .requireNonNull(executor, "ConnectionExecutor<T> executor")
                        .execute(connection);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new PersistenceException("Connection has failed", e);
        }
    }

    private Connection getConnection() throws SQLException {
        // Obtain a connection using the connection loader details
        return DriverManager.getConnection(
                PersistenceValidator.requireNonNull(loader.getUrl(), "String url"),
                PersistenceValidator.requireNonNull(loader.getUsername(), "String username"),
                PersistenceValidator.requireNonNull(loader.getPassword(), "String password"));
    }

    /**
     * Gets the logger associated with this {@code ConnectionManager}.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Executes database migrations by applying SQL scripts from the specified
     * directory.
     * Each SQL script file in the directory must follow the naming convention
     * "V{version}__{description}.sql", where {version} is a numeric version and
     * {description} is a descriptive string.
     *
     * <p>
     * The method reads migration information from the "migration_info" table to
     * track
     * applied migrations. If a migration with a specific version is not found in
     * the
     * "migration_info" table, it is considered new, and the corresponding SQL
     * script
     * is executed. After successful execution, the version information is inserted
     * into the "migration_info" table to mark the completion of the migration.
     * </p>
     *
     * @param path the path to the directory containing SQL script files for
     *             migrations
     * @throws PersistenceException if there is an error accessing the specified
     *                              directory, if the SQL files are invalid or
     *                              inaccessible, or if any migration operation
     *                              fails
     */
    public void executeMigration(String path) {
        File directory = Path.of(path).toFile();
        if (!directory.isDirectory()) {
            throw new PersistenceException("Could not access directory, make sure it exists");
        }

        // Retrieve the list of already applied migration versions
        List<String> versions = execute(connection -> getMigrationInfo(connection));

        // Iterate over each SQL script file in the directory
        for (File file : directory.listFiles()) {
            String name = file.getName();
            Pattern pattern = Pattern.compile("^V(\\d+)__(.+\\.sql)$");
            Matcher matcher = pattern.matcher(name);

            if (!file.isFile() || !file.canRead() || !matcher.matches()) {
                throw new PersistenceException("Unable to access SQL files, make sure the file is valid or accessible");
            }

            String version = name.replaceAll("[^V(\\d+)]", "").strip();
            // Check if the migration version has already been applied
            if (!versions.contains(version)) {
                // Execute the migration script
                execute(connection -> {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file));
                            Statement statement = connection.createStatement()) {

                        StringBuilder builder = new StringBuilder();

                        int character;
                        while ((character = reader.read()) != -1) {
                            builder.append((char) character);
                        }

                        // Split the script into individual queries and execute each one
                        String[] queries = builder.toString().split(";");
                        for (String query : queries) {
                            statement.execute(query);
                        }

                        // Mark the migration as completed by inserting version into the
                        // "migration_info" table
                        statement.execute("INSERT INTO migration_info VALUES ('" + version + "')");
                        connection.commit();

                        logger.info("The migration has been successfully executed");

                        return Void.TYPE;
                    } catch (SQLException | IOException e) {
                        // Rollback the transaction if an error occurs during migration
                        connection.rollback();
                        throw new PersistenceException("Unable to perform migration", e);
                    }
                });
            }
        }
    }

    private List<String> getMigrationInfo(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS migration_info(migration_version VARCHAR(255) PRIMARY KEY)";
            statement.execute(connection.nativeSQL(query));
            ResultSet set = statement.executeQuery("SELECT migration_version FROM migration_info");
            List<String> versions = new ArrayList<>();
            while (set.next()) {
                versions.add(set.getString("migration_version"));
            }
            return versions;
        } catch (SQLException e) {
            throw new PersistenceException("Unable to perform migration", e);
        }
    }
}
