import javax.servlet.*;

import java.util.concurrent.*;

public class BackgroundJobManager implements ServletContextListener {
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public static int initiatedJobs = 0;
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
    	DatabaseHandler db = new DatabaseHandler();
    	for (int i = 1; i <= db.getElementCount("service"); i++) {
    		LogServiceUptime ls = new LogServiceUptime(db.getService(i));
    		if (ls != null) {
	    		scheduler.scheduleAtFixedRate(ls, 0, 1120, TimeUnit.SECONDS);
	    		System.out.println(scheduler);
	    		initiatedJobs++;	
    		}
    	}   
    }

    public void addContext(int serviceId) {
    	DatabaseHandler db = new DatabaseHandler();
    	LogServiceUptime ls = new LogServiceUptime(db.getService(serviceId));
    	scheduler.scheduleAtFixedRate(ls, 0, 1120, TimeUnit.SECONDS);
    	initiatedJobs++;
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }

}