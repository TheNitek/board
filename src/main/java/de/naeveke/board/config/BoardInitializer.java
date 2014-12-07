package de.naeveke.board.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.Log4jConfigListener;

/**
 *
 * @author Nitek
 */
public class BoardInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) {
        //container.setInitParameter("log4jConfigLocation", "/WEB-INF/spring/log4j.xml");
        //container.addListener(new Log4jConfigListener());
        
        XmlWebApplicationContext rootContext
                = new XmlWebApplicationContext();
        rootContext.setConfigLocation("/WEB-INF/spring/board-context.xml");

        container.addListener(new ContextLoaderListener(rootContext));
        
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");

        ServletRegistration.Dynamic dispatcher
                = container.addServlet("dispatcher", new DispatcherServlet(appContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
