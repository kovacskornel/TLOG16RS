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
import org.avaje.agentloader.AgentLoader;
import com.kovacskornel.tlog16rs.resources.TestEntity;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Contexts;
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

    
    private final DataSourceConfig dataSourceConfig;
	private final ServerConfig serverConfig;
    private EbeanServer ebeanServer;
    
    public CreateDatabase(TLOG16RSConfiguration config)
    {
        try {updateSchema(config);}
        catch (LiquibaseException | SQLException | ClassNotFoundException a)
        {
            System.out.println(a.getMessage());
        }
        agentLoader();
        dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver(config.getDbDriver());
        dataSourceConfig.setUrl(config.getDbUrl());
        dataSourceConfig.setUsername(config.getDbUsername());
        dataSourceConfig.setPassword(config.getDbPassword());
        serverConfig = new ServerConfig();
        serverConfig.setName(config.getDbName());
        serverConfig.setDdlGenerate(true);
        serverConfig.setDdlRun(true);
        serverConfig.setDefaultServer(true);
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TestEntity.class);  
        ebeanServer = EbeanServerFactory.create(serverConfig);
    }
    private void updateSchema(TLOG16RSConfiguration config) throws LiquibaseException, SQLException, ClassNotFoundException{
		Liquibase liquibase;
        liquibase = new Liquibase("migrations.xml",new ClassLoaderResourceAccessor(),new JdbcConnection(DriverManager.getConnection(config.getDbDriver(),config.getDbUsername(),config.getDbPassword())));
		liquibase.update(new Contexts());
    }
    
    private void agentLoader() {
        if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages=com.kovacskornel.tlog16rs.**")) {
        System.err.println("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
        }
    }
    

}