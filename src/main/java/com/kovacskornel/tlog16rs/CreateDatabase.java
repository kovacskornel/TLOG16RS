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

/**
 *
 * @author precognox
 */

@lombok.Getter
@lombok.Setter
public class CreateDatabase {

    
    private DataSourceConfig dataSourceConfig;
	private ServerConfig serverConfig;
    public EbeanServer ebeanServer;
    
    
    
    public CreateDatabase()
    {
        agentLoader();
        dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver("org.mariadb.jdbc.Driver");
        dataSourceConfig.setUrl("jdbc:mariadb://127.0.0.1:9005/timelogger");
        dataSourceConfig.setUsername("timelogger");
        dataSourceConfig.setPassword("633Ym2aZ5b9Wtzh4EJc4pANx");
        serverConfig = new ServerConfig();
        serverConfig.setName("timelogger");
        serverConfig.setDdlGenerate(true);
        serverConfig.setDdlRun(true);
        serverConfig.setRegister(false);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TestEntity.class);  
        ebeanServer = EbeanServerFactory.create(serverConfig);
    }
    
    private void agentLoader() {
        if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages=com.kovacskornel.tlog16rs.**")) {
        System.err.println("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
        }
    }
    

}