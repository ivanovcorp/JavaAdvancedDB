package orm.strategies.schema_init_strategies;

import orm.ClassEntityScanner;
import orm.strategies.TableCreator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class DropCreateStrategy extends SchemaInitializationStrategyAbstract {

    protected DropCreateStrategy(Connection connection,
                                 TableCreator creator,
                                 String dataSource,
                                 ClassEntityScanner entityScanner) {
        super(connection, creator, dataSource, entityScanner);
    }

    @Override
    public void execute() throws SQLException {
        String query = "DROP DATABASE IF EXISTS `" + super.dataSource + "`;";
        this.connection.prepareStatement(query).executeUpdate();
        query = "CREATE DATABASE `" + this.dataSource + "`;";
        this.connection.prepareStatement(query).execute();
        this.createTables(this.scanForEntities());
    }

    private void createTables(Map<String, Class> entities) throws SQLException {
        for (Class entity: entities.values()){
            this.creator.doCreate(entity);
        }
    }
}


