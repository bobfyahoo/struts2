package dao;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@Slf4j
public class DatabaseContextListener
        implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Database.initialize();
     /*   try {
            Database.initializeSchema();
        } catch (SQLException e) {
            log.error("initializeSchema", e);
            throw new RuntimeException(e);
        }*/
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Database.shutdown();
    }
}