package orm.strategies.schema_init_strategies;

import orm.ClassEntityScanner;
import orm.strategies.TableCreator;

import java.sql.Connection;
import java.sql.SQLException;

public class DoNothingStrategy extends SchemaInitializationStrategyAbstract {
    protected DoNothingStrategy(Connection connection, TableCreator creator, String dataSource, ClassEntityScanner cec) {
        super(connection, creator, dataSource, cec);
    }

    @Override
    public void execute() throws SQLException {

    }
}
