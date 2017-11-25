package orm.strategies;

import annotations.Column;
import annotations.Entity;
import annotations.Id;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTableCreator implements TableCreator {
    private Connection connection;
    private String dataSource;

    public DatabaseTableCreator(Connection connection, String dataSource){
        this.connection = connection;
        this.dataSource = dataSource;
    }

    public void doCreate(Class entity) throws SQLException {
        String tableName = this.getTableName(entity);
        String query = " CREATE TABLE IF NOT EXISTS " + this.dataSource + "." + tableName + "( ";

        String columns = "";

        Field[] fields = entity.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);

            if (field.isAnnotationPresent(Id.class)) {
                columns += "`" + this.getFieldName(field) + "` "
                        + " INT " +
                        " PRIMARY KEY AUTO_INCREMENT ";
            } else {
                columns += "`" + this.getFieldName(field) + "` "
                        + this.getDatabaseType(field);
            }

            if (i < fields.length - 1) {
                columns += ", ";
            }
        }

        query += columns + ")";
        connection.prepareStatement(query).execute();
    }

    public String getFieldName(Field field) {
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

    public String getTableName(Class<?> entity){
        String tableName = "";
        if(entity.isAnnotationPresent(Entity.class)){
            Entity annotation = entity.getAnnotation(Entity.class);
            tableName = annotation.name();
        }
        if(tableName.equals("")){
            tableName = entity.getClass().getSimpleName();
        }
        return tableName;
    }

    public String getDatabaseType(Field field){
        field.setAccessible(true);
        switch (field.getType().getSimpleName()){
            case "int":
            case "Integer":
                return "INT";
            case "String":
                return "VARCHAR(50)";
            case "Date":
                return "DATETIME";
        }
        return null;
    }
}

