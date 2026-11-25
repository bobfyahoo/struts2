package dao;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DatabaseContextListener
        implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Database.initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Database.shutdown();
    }
}