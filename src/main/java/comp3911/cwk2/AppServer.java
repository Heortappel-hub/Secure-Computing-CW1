package comp3911.cwk2;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;

public class AppServer {
  public static void main(String[] args) throws Exception {
    Log.setLog(new StdErrLog());

    // Enable sessions so HttpSession calls in AppServlet work
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    context.addServlet(AppServlet.class, "/*");

    Server server = new Server(8080);
    server.setHandler(context);

    server.start();
    server.join();
  }
}
