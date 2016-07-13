package com.jims.his;

import com.google.inject.Singleton;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.jims.his.filter.CorsFilter;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by heren on 2015/8/24.
 */
public class CompsiteModul extends JerseyServletModule {

    @Override
    protected void configureServlets() {

        bind(GuiceContainer.class) ;
        bind(CorsFilter.class).in(Singleton.class);
        //bind(RelamFilter.class).in(Singleton.class);
        filter("/api/*").through(CorsFilter.class);
        Map<String, String> params = new HashMap<String, String>();
//        params.put("jersey.config.server.provider.packages", "com.wiki.services");//Jersey 2.0
        params.put("com.sun.jersey.config.property.packages", "com.jims.his.service"); //PROPERTY_PACKAGES
        params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");

        serve("/api/*").with(GuiceContainer.class, params);
        install(new JpaPersistModule("domain"));
//        install(new JpaPersistModule("domain").properties(mySqlProperties()));
        filter("/api/*").through(PersistFilter.class);
        //filter("/api/*").through(RelamFilter.class);

    }
}
