import javax.servlet.*;


import java.util.concurrent.*;

public class BackgroundJobManager implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        LogServiceUptime ls = new LogServiceUptime("http://docs.uscrpl.com/confluence/");
        scheduler.scheduleAtFixedRate(ls, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }

}