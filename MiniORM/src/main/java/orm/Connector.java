package orm;

import orm.persistence.EntityManagerBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connector {
    private EntityManagerBuilder builder;
    private String adapter;
    private String driver;
    private String host;
    private String port;
    private String dbName;
    private String user;
    private String pass;

    public Connector(EntityManagerBuilder builder) {
        this.builder = builder;
    }

    public Connector setAdapter(String adapter) {
        this.adapter = adapter;
        return this;
    }

    public Connector setDriver(String driver) {
        this.driver = driver;
        return this;
    }

    //Implement other setter methods

    public EntityManagerBuilder createConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", this.user);
        properties.setProperty("password", this.pass);
        this.builder.setConnection(DriverManager.getConnection(String.format("%s:%s://%s:%s/",
                this.adapter, this.driver, this.host, this.port),
                properties));
        return this.builder;
    }


    public Connector setHost(String host) {
        this.host = host;
        return this;
    }

    public Connector setPort(String port) {
        this.port = port;
        return this;
    }

    public Connector setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public Connector setUser(String user) {
        this.user = user;
        return this;
    }

    public Connector setPass(String pass) {
        this.pass = pass;
        return this;
    }


}
