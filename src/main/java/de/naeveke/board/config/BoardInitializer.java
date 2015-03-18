package de.naeveke.board.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 *
 * @author Nitek
 */
public class BoardInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) {
        
        XmlWebApplicationContext rootContext
                = new XmlWebApplicationContext();
        rootContext.setConfigLocation("/WEB-INF/spring/board-context.xml");

        container.addListener(new ContextLoaderListener(rootContext));
        
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");

        ServletRegistration.Dynamic dispatcher
                = container.addServlet("dispatcher", new DispatcherServlet(appContext));
        dispatcher.setAsyncSupported(true);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
