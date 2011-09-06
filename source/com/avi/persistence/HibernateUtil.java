package com.avi.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Startup Hibernate and provide access to the singleton SessionFactory
 */
public class HibernateUtil
{

    private static SessionFactory sessionFactory1;
    private static SessionFactory sessionFactory2;
    protected static Logger log = LoggerFactory.getLogger( HibernateUtil.class );

    static
    {
        try
        {
            sessionFactory1 = new Configuration().configure( "hibernate.cfg1.xml" ).buildSessionFactory();
            sessionFactory2 = new Configuration().configure( "hibernate.cfg2.xml" ).buildSessionFactory();
        }
        catch ( Throwable ex )
        {
            throw new ExceptionInInitializerError( ex );
        }
    }

    public static SessionFactory getSessionFactory1()
    {
        // Alternatively, we could look up in JNDI here
        return sessionFactory1;
    }

    public static SessionFactory getSessionFactory2()
    {
        // Alternatively, we could look up in JNDI here
        return sessionFactory2;
    }

    public static void shutdown()
    {
        // Close caches and connection pools
        log.info( "###########################################   Caliing Shutdown...... " );
        getSessionFactory1().close();
        getSessionFactory2().close();
    }
}
