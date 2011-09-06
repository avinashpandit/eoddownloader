package com.avi.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyMBeanC
{
    private static PropertyMBeanC instance = new PropertyMBeanC();
    private static Properties properties;
    private static Logger log = LoggerFactory.getLogger( PropertyMBeanC.class );

    private PropertyMBeanC()
    {
        properties = new Properties();
        try
        {
            File file = new File( "avitrader.properties" );
            properties.load( new FileInputStream( file.getAbsolutePath() ) );
        }
        catch ( IOException e )
        {
            log.error( "Unable to load properties file.", e );
        }
    }

    public static PropertyMBeanC getInstance()
    {
        return instance;
    }

    public String getEODDataFolder()
    {
        return getProperty( "Trader.EODData.FolderPath", "C:\\MyTrading\\EODData\\" );
    }

    public String getSMARTSymbolList()
    {
        return getProperty( "Trader.Google.SMART.SymbolList", "" );
    }


    public String getGoogleCSVAddress()
    {
        return getProperty( "Trader.EODData.Google.CSV.Address", "http://www.google.com/finance/historical?q=" );
    }

    public String getGoogleNSEAddress()
    {
        return getProperty( "Trader.EODData.Google.NSE.Address", "http://www.google.com/finance/historical?q=NSE:" );
    }

    public String getDukascopyUserName()
    {
        return getProperty( "Trader.Dukascopy.UserName", "DEMO2mwEZN" );
    }

    public String getDukascopyPassword()
    {
        return getProperty( "Trader.Dukascopy.Password", "mwEZN" );
    }

    public String getDukascopyDownloadPeriod()
    {
        return getProperty( "Trader.Dukascopy.Download.Period", "ONE_MIN" );
    }

    public int getDukascopyDownloadNumBars()
    {
        return Integer.parseInt( getProperty( "Trader.Dukascopy.Download.NumBars", "50000" ) );
    }

    public String getProperty( String key, String defaultVal )
    {
        String val = properties.getProperty( key );
        if ( val == null )
        {
            return defaultVal;
        }
        else
        {
            if ( val.startsWith( "\"" ) )
            {
                val = val.substring( 1, val.length() - 1 );
            }
            return val;
        }
    }

    public String getMainDataSQLUrl()
    {
        return getProperty( "Trader.SQL.MAIN.SQLConnectStr", "jdbc:mysql://localhost:3306/main" );
    }

    public String getEODDataSQLUrl()
    {
        return getProperty( "Trader.SQL.EOD.SQLConnectStr", "jdbc:mysql://localhost:3306/quotes_0" );
    }

    public String getIntradayDataSQLUrl()
    {
        return getProperty( "Trader.SQL.INTRADAY.SQLConnectStr", "jdbc:mysql://localhost:3306/intraday" );
    }

    public String getForexDataFolder()
    {
        return getProperty( "Trader.ForexData.FolderPath", "C:\\MyTrading\\ForexData\\" );
    }

    public int getMinTrades()
    {
        return Integer.parseInt( getProperty( "Trader.MinTrades", "30" ) );
    }

}
