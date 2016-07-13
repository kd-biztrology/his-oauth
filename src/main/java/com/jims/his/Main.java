package com.jims.his;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
//import org.flywaydb.core.Flyway;

/**
 * Created by heren on 2014/10/13.
 */
public class Main {
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        String webappDirLocation = "src/main/webapp/";

        // The port that we should run on can be set into an environment variable
        // Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8888";
        }

        File file = new File(webappDirLocation) ;
        if(file.exists()){
            System.out.println(file.getAbsolutePath());
        }else{
            System.out.println("路径有问题");
        }

        Server server = new Server(Integer.valueOf(webPort));
        WebAppContext root = new WebAppContext();

        root.setContextPath("/");
        root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);


        root.setParentLoaderPriority(true);
        //migrationDb();
        server.setHandler(root);
        server.start();
        server.join();
    }


    //public static void migrationDb(){
    //    Flyway flyway = new Flyway();
    //
    //    //设置数据库链接配置
    //    flyway.setDataSource("jdbc:oracle:thin:@127.0.0.1:1521:orcl","system","oracle");
    //
    //    //设置schema用户
    //    flyway.setSchemas("PHAR");
    //    flyway.setTable("SCHEMA_VERSION");
    //    flyway.setEncoding("UTF-8");
    //    flyway.setValidateOnMigrate(true);
    //
    //    //清空所有表结构
    //    //flyway.clean();
    //    //初始化flyWAy
    //    //flyway.init();
    //    //执行版本控制
    //    //flyway.migrate();
    //}
}
