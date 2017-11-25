package orm.strategies.schema_init_strategies;

import java.sql.SQLException;

public interface SchemaInitializationStrategy  {
    void execute() throws SQLException;
}

