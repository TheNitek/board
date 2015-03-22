package de.naeveke.board.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.Log4jConfigListener;

/**
 *
 * @author Nitek
 */
public class BoardInitializer implements WebApplicationInitializer {

    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public void onStartup(ServletContext servletContext) {
        servletContext.setInitParameter("log4jConfigLocation", "classpath:log4j.properties");
        // Fixes deploy problems (see http://stackoverflow.com/a/5014810/3155154)
        servletContext.setInitParameter("log4jExposeWebAppRoot", "false");
        servletContext.addListener(new Log4jConfigListener());

        AnnotationConfigWebApplicationContext rootContext
                = new AnnotationConfigWebApplicationContext();
        rootContext.register(BoardConfig.class);

        servletContext.addListener(new ContextLoaderListener(rootContext));

        AnnotationConfigWebApplicationContext appContext
                = new AnnotationConfigWebApplicationContext();
        appContext.register(DispatcherConfig.class);

        ServletRegistration.Dynamic dispatcher
                = servletContext.addServlet("dispatcher", new DispatcherServlet(appContext));
        dispatcher.setAsyncSupported(true);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        logger.info("finished onStartup");
    }
}
