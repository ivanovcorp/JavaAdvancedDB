package orm.persistence;

import annotations.Column;
import annotations.Entity;
import annotations.Id;
import orm.strategies.schema_init_strategies.SchemaInitializationStrategy;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class EntityManager implements DbContext {
    private Connection connection;
    private String dataSource;
    private SchemaInitializationStrategy strategy;

    public EntityManager(Connection connection,
                         String dataSource,
                         SchemaInitializationStrategy strategy)
            throws SQLException {
        this.connection = connection;
        this.dataSource = dataSource;
        this.strategy = strategy;
        this.strategy.execute();
    }

    @Override
    public <E> boolean persist(E entity) throws IllegalAccessException, SQLException {
        Field primary = this.getId(entity.getClass());
        primary.setAccessible(true);
        Object value = primary.get(entity);

        if (value == null || (Integer) value <= 0) {
            return this.doInsert(entity, primary);
        }
        return this.doUpdate(entity, primary);
    }

    @Override
    public <E> Iterable<E> find(Class<E> table) {
        return null;
    }

    @Override
    public <E> Iterable<E> find(Class<E> table, String where) {
        return null;
    }

    @Override
    public <E> E findFirst(Class<E> table) {
        return null;
    }

    @Override
    public <E> E findFirst(Class<E> table, String where) throws SQLException, IllegalAccessException, InstantiationException {
        String query = "SELECT * FROM " + this.getTableName(table) + " WHERE" + where + " LIMIT 1";
        Statement stmt = this.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        rs.next();
        E entity = table.newInstance();
        this.fillEntity(table, rs, entity);
        return entity;
    }

    private <E> boolean doUpdate(E entity, Field primary) throws IllegalAccessException, SQLException {
        String query = "UPDATE " + this.dataSource + "." + this.getTableName(entity.getClass()) + " SET ";
        String fieldsNamesAndValues = "";
        String where = "";

        Field[] fields = entity.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field currField = fields[i];
            currField.setAccessible(true);

            if (currField.getName().equals(primary.getName())) {
                where += " WHERE `" + currField.getAnnotation(Column.class).name() + "` = " + currField.get(entity);
                continue;
            } else {
                if (currField.get(entity) instanceof Date) {
                    fieldsNamesAndValues += "`" + currField.getAnnotation(Column.class).name() + "` = '"
                            + new SimpleDateFormat("yyyy-MM-dd").format(currField.get(entity)) + "'";
                } else {
                    fieldsNamesAndValues += "`" + currField.getAnnotation(Column.class).name() + "` = '"
                            + currField.get(entity) + "'";
                }
            }

            if (i < fields.length - 1) {
                fieldsNamesAndValues += ",";
            }
        }
        query += fieldsNamesAndValues + where;
        return this.connection.prepareStatement(query).execute();
    }

    private <E> boolean doInsert(E entity, Field primary) throws IllegalAccessException, SQLException {
        String tableName = this.getTableName(entity.getClass());
        String query = "INSERT INTO " + this.dataSource + "." + tableName + " (";
        String fieldNames = "";
        String values = "";

        Field[] fields = entity.getClass().getDeclaredFields();
        for (int i = 1; i < fields.length; i++) {
            Field currentField = fields[i];
            currentField.setAccessible(true);

            fieldNames += '`' + currentField.getAnnotation(Column.class).name() + "`";

            if (currentField.get(entity) instanceof Date) {
                values += "'" + new SimpleDateFormat("yyyy-MM-dd").format(currentField.get(entity)) + "'";
            } else {
                values += "'" + currentField.get(entity) + "'";
            }

            if (i < fields.length - 1) {
                fieldNames += ",";
                values += ",";
            }
        }
        query += fieldNames + ") VALUES (" + values + ");";
        return this.connection.prepareStatement(query).execute();
    }

    private Field getId(Class entity) {
        return Arrays.stream(entity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Object does not have primary key"));
    }

    private String getFieldName(Field field) {
        String fieldName = "";

        if (field.isAnnotationPresent(Column.class)) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            fieldName = columnAnnotation.name();
        }

        if (fieldName.equals("")) {
            fieldName = field.getName();
        }

        return fieldName;
    }

    private <E> String getTableName(Class<?> entity) {
        String tableName = "";
        if (entity.isAnnotationPresent(Entity.class)) {
            Entity annotation = entity.getAnnotation(Entity.class);
            tableName = annotation.name();
        }
        if (tableName.equals("")) {
            tableName = entity.getClass().getSimpleName();
        }
        return tableName;
    }

    private <E> void fillEntity(Class table, ResultSet rs, E entity) throws SQLException, IllegalAccessException {
        Field[] fields = table.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field currentField = fields[i];
            currentField.setAccessible(true);
            this.fillField(currentField, rs,
                    currentField.getAnnotation(Column.class).name(), entity);
        }
    }

    private <E> void fillField(Field currentField, ResultSet rs, String name, Object entity)
            throws SQLException, IllegalAccessException {
        currentField.setAccessible(true);
        if (currentField.getType() == Integer.class
                || currentField.getType() == int.class) {
            currentField.set(entity, rs.getInt(name));
        } else if (currentField.getType() == String.class) {
            currentField.set(entity, rs.getString(name));
        } else if (currentField.getType() == Date.class) {
            currentField.set(entity, rs.getDate(name));
        }
    }
}