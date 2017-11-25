package orm.strategies.schema_init_strategies;

import orm.ClassEntityScanner;
import orm.strategies.TableCreator;

import java.sql.Connection;
import java.util.Map;

abstract class SchemaInitializationStrategyAbstract
        implements SchemaInitializationStrategy {
    private ClassEntityScanner entityScanner;
    TableCreator creator;
    Connection connection;
    String dataSource;

    SchemaInitializationStrategyAbstract(
            Connection connection,
            TableCreator creator,
            String dataSource,
            ClassEntityScanner entityScanner){
        this.creator = creator;
        this.connection = connection;
        this.dataSource = dataSource;
        this.entityScanner = entityScanner;
    }

    Map<String, Class> scanForEntities(){
        return this.entityScanner
                .listFilesForFolder(System.getProperty("user.dir"));
    }
}









