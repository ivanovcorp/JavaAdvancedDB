package orm.persistence;

import orm.ClassEntityScanner;
import orm.strategies.DatabaseTableCreator;
import orm.strategies.schema_init_strategies.SchemaInitializationStrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class StrategyConfigurer {
    private EntityManagerBuilder builder;

    StrategyConfigurer(EntityManagerBuilder builder){
        this.builder = builder;
    }

    public <T extends SchemaInitializationStrategy> EntityManagerBuilder
            set(Class<T> strategyClass)
            throws IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        Constructor<SchemaInitializationStrategy> constructor =
                ((Constructor<SchemaInitializationStrategy>) strategyClass.getDeclaredConstructors()[0]);
        constructor.setAccessible(true);
        SchemaInitializationStrategy strategy = constructor.newInstance(this.builder.getConnection(),
                new DatabaseTableCreator(this.builder.getConnection(), this.builder.getDataSource()),
                this.builder.getDataSource(), new ClassEntityScanner());
        this.builder.setStrategy(strategy);
        return this.builder;
    }
}
