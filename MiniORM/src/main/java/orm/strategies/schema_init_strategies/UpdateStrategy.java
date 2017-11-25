package orm.strategies.schema_init_strategies;

import annotations.Column;
import annotations.Entity;
import annotations.Id;
import orm.ClassEntityScanner;
import orm.strategies.TableCreator;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateStrategy extends SchemaInitializationStrategyAbstract {
    protected UpdateStrategy(Connection connection,
                             TableCreator creator,
                             String dataSource,
                             ClassEntityScanner cec) {
        super(connection, creator, dataSource, cec);
    }

    @Override
    public void execute() throws SQLException {
        List<String> dbTables = this.getDatabaseTables();
        for (Class entity : this.scanForEntities().values()) {
            Entity annotation = (Entity) entity.getAnnotation(Entity.class);
            String tableName = annotation.name();
            if (dbTables.contains(tableName)) {
                this.checkTableFields(entity, tableName);
            } else {
                this.creator.doCreate(entity);
            }
        }
    }

    private List<String> getDatabaseTables() throws SQLException {
        ResultSet rs = this.connection.getMetaData()
                .getTables(this.dataSource,null, "%", null);
        List<String> existingTables = new ArrayList<>();
        while (rs.next()) {
            existingTables.add(rs.getString(3));
        }
        return existingTables;
    }

    private void checkTableFields(Class entity,String tableName)
            throws SQLException {
        String query = "SELECT column_name FROM information_schema.columns" +
                " WHERE table_schema = 'orm_db'" +
                " AND table_name = '" + tableName + "'";
        ResultSet rs = this.connection.prepareStatement(query).executeQuery();
        List<String> columns = new ArrayList<>();
        while(rs.next()) {
            String currentColumnName = rs.getString("column_name");
            columns.add(currentColumnName);
        }
        for (Field currField: entity.getDeclaredFields()) {
            String columnName = currField.getAnnotation(Column.class).name();
            if(!columns.contains(columnName)){
                this.addFieldToTable(tableName,currField);
            }
        }
    }

    private void addFieldToTable(String tableName, Field field)
            throws SQLException {
        String query = " ALTER TABLE " + this.dataSource + "." + tableName +
                " ADD COLUMN `" + field.getAnnotation(Column.class).name() + "` " + this.creator.getDatabaseType(field) + " ";
        if(field.getAnnotation(Id.class) != null){
            query += " PRIMARY KEY AUTO_INCREMENT";
        }
        this.connection.prepareStatement(query).execute();
    }
}
