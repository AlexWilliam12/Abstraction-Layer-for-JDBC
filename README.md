# Abstraction layer for using JDBC

## Overview

This project is a layer for working in an abstracted, secure and reusable way with the JDBC interface, ensuring that all resources are closed automatically and allowing an approach for working with Java Records, which bring a more secure approach to data traffic in applications.

## Features

- ### Declarative way to create SQL query statements and map the results. Implement references to static methods to map results dynamically.

```java
// Example

public final class ExampleService {

    private final PersistenceUnit persistenceUnit;

    public ExampleService(PersistenceUnit persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }

    public List<Example> getList() {
        return persistenceUnit.persist(query -> query
                .setQuery("SELECT * FROM example"))
                .execute(Example::collectAll);
    }

    public Example getSingle(int id) {
        return persistenceUnit.persist(query -> query
                .setQuery("SELECT * FROM example WHERE id = ?")
                .setArgs(id))
                .execute(Example::collectSingle)
                .orElseThrow(() -> new NoSuchElementException());
    }

    public Example insert(Example example) {
        return persistenceUnit.persist(query -> query
                .setQuery("INSERT INTO example VALUES (?, ?)")
                .setArgs(example.getValues()))
                .execute(map -> Example.collectInsertResult(map, example))
                .orElseThrow(() -> new NoSuchElementException());
    }

    public Example update(Example example) {
        return persistenceUnit.persist(query -> query
                .setQuery("UPDATE example SET name = ? WHERE id = ?")
                .setArgs(example.name(), example.id()))
                .execute(map -> Example.collectUpdateResult(map, example))
                .get();
    }

    public boolean delete(int id) {
        return persistenceUnit.persist(query -> query
                .setQuery("DELETE FROM example WHERE id = ?")
                .setArgs(id))
                .execute(Example::collectDeleteResult)
                .get();
    }
}
```

```java
// Instead this

public List<Example> getList() {
    return persistenceUnit.persist(query -> query
            .setQuery("SELECT * FROM example"))
            .execute(map -> {
                List<Example> examples = new ArrayList<>();
                while (map.hasNext()) {
                    examples.add(
                        new Example(
                            map.getColumn("id"),
                            map.getColumn("name")));
                }
                return examples;
            });
}

// Use this

public List<Example> getList() {
        return persistenceUnit.persist(query -> query
                .setQuery("SELECT * FROM example"))
                .execute(Example::collectAll);
}
```


```java
// Entity used

public record Example(int id, String name) {

    public static Optional<Example> collectSingle(MappedResult result) {
        return result.hasNext()
                ? Optional.ofNullable(
                        new Example(
                                result.getColumn("id"),
                                result.getColumn("name")))
                : Optional.ofNullable(null);
    }

    public static List<Example> collectAll(MappedResult result) {
        List<Example> examples = new ArrayList<>();
        while (result.hasNext()) {
            examples.add(
                    new Example(
                            result.getColumn("id"),
                            result.getColumn("name")));
        }
        return examples;
    }

    public static Optional<Example> collectInsertResult(MappedResult result, Example example) {
        return result.hasNext()
                ? Optional.ofNullable(
                        new Example(
                                result.getGeneratedKey(),
                                example.name))
                : Optional.ofNullable(null);
    }

    public static Supplier<Example> collectUpdateResult(MappedResult result, Example example) {
        return () -> {
            if (result.getRowsAffected() == 0) {
                throw new IllegalArgumentException();
            }
            return example;
        };
    }

    public static Supplier<Boolean> collectDeleteResult(MappedResult result) {
        return () -> result.getRowsAffected() > 0;
    }

    public Object[] getValues() {
        return new Object[] { id, name };
    }
}
```

- ### Implement the connection in a flexible way that integrates your application using the ConnectionLoader interface.

```java
// Example

public final class ConnectionLoaderImplemented implements ConnectionLoader {

    private String url;
    private String username;
    private String password;
    private String driver;

    public ConnectionLoaderImplemented(String path) {
        loader(Path.of(path));
    }

    private void loader(Path path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("DATABASE_URL")) {
                    url = getProperty(line);
                } else if (line.contains("DATABASE_USERNAME")) {
                    username = getProperty(line);
                } else if (line.contains("DATABASE_PASSWORD")) {
                    password = getProperty(line);
                } else if (line.contains("DATABASE_DRIVER")) {
                    driver = getProperty(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getProperty(String property) {
        return property.substring(property.lastIndexOf("=") + 1);
    }

    @Override
    public String getDriver() {
        return driver;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
```

- ### Support for versioned migrations

```java
// Example

ConnectionManager manager = new ConnectionManager(loader);
manager.executeMigration("src/main/resources/migrations");
```

- ### All operations are unique, transactional and with explanatory logs and exceptions.