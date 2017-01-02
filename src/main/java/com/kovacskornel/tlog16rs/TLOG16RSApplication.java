package com.kovacskornel.tlog16rs;

import com.avaje.ebean.Ebean;
import com.kovacskornel.tlog16rs.resources.TLOG16RSResource;
import com.avaje.ebean.EbeanServer;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TLOG16RSApplication extends Application<TLOG16RSConfiguration> {

    public static void main(final String[] args) throws Exception {
        new TLOG16RSApplication().run(args);
    }

    @Override
    public String getName() {
        return "TLOG16RS";
    }
    
    @Override
    public void initialize(final Bootstrap<TLOG16RSConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final TLOG16RSConfiguration configuration,
                    final Environment environment) {
        final CreateDatabase database = new CreateDatabase(configuration);
        final EbeanServer ebeanServer;
        ebeanServer = database.getEbeanServer();
        database.setEbeanServer(ebeanServer);
        environment.jersey().register(new TLOG16RSResource());
    }

}
