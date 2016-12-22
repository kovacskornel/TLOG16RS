/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kovacskornel.tlog16rs.resources;

import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import org.avaje.agentloader.AgentLoader;


/**
 *
 * @author precognox
 */

@lombok.Getter
@lombok.Setter
public class CreateDatabase {

    private DataSourceConfig dataconf = null;
	private ServerConfig servconf = null;
    
    dataconf = new DataSourceConfig();
    
    private void agentLoader() {
  if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages=com.yourname.tlog16rs.**")) {
   System.err.println("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
  }
 }

}

class TestEntity {
    String text;
    int id;
}
