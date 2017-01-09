/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kovacskornel.tlog16rs;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.kovacskornel.tlog16rs.resources.Task;
import com.kovacskornel.tlog16rs.resources.TimeLogger;
import com.kovacskornel.tlog16rs.resources.WorkDay;
import com.kovacskornel.tlog16rs.resources.WorkMonth;
import org.avaje.agentloader.AgentLoader;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;


/**
 *
 * @author precognox
 */

@lombok.Getter
@lombok.Setter
public class CreateDatabase {

    private static final Logger LOGGER = Logger.getLogger(CreateDatabase.class.getName());
    private final DataSourceConfig dataSourceConfig;
	private final ServerConfig serverConfig;
    private EbeanServer ebeanServer;
    
    public CreateDatabase(TLOG16RSConfiguration config)
    {
        
        try {
            updateSchema(config);
        }
        catch (LiquibaseException |
                SQLException |
                ClassNotFoundException a)
        {
            LOGGER.info(a.getMessage());
        }
        agentLoader();
        dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver(config.getDbDriver());
        dataSourceConfig.setUrl(config.getDbUrl());
        dataSourceConfig.setUsername(config.getDbUsername());
        dataSourceConfig.setPassword(config.getDbPassword());
        serverConfig = new ServerConfig();
        serverConfig.setName(config.getDbName());
        serverConfig.setDdlGenerate(false);
        serverConfig.setDdlRun(false);
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TimeLogger.class);  
        serverConfig.addClass(WorkDay.class);
        serverConfig.addClass(WorkMonth.class);
        serverConfig.addClass(Task.class);
        serverConfig.setDefaultServer(true);
        ebeanServer = EbeanServerFactory.create(serverConfig);
    }
    
private void updateSchema(TLOG16RSConfiguration config) throws ClassNotFoundException, LiquibaseException, SQLException {
        Class.forName(config.getDbDriver());
        Liquibase liquibase = new Liquibase("migrations.xml",
                new ClassLoaderResourceAccessor(),
                new JdbcConnection(DriverManager.getConnection(config.getDbUrl(), config.getDbUsername(), config.getDbPassword())));
        liquibase.update(new Contexts(), new LabelExpression());
    }
    
    private void agentLoader() {
        if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages=com.kovacskornel.tlog16rs.**")) {
        LOGGER.info("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
        }
   }
}