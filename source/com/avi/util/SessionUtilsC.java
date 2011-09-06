package com.avi.util;

import com.avi.persistence.HibernateUtil;
import org.hibernate.Session;

public class SessionUtilsC
{

    public static Session getMainSession()
    {
        return HibernateUtil.getSessionFactory1().openSession();
    }

    public static Session getIntradaySession()
    {
        return HibernateUtil.getSessionFactory2().openSession();
    }

    public static Session getDailySession()
    {
        return HibernateUtil.getSessionFactory1().openSession();
    }

}
