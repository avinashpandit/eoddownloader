package com.avi.downloader.InteractiveBrokers;

import com.avi.core.Symbol;
import com.avi.util.DownloaderUtilC;
import com.avi.util.SessionUtilsC;
import com.ib.client.Contract;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public abstract class DBWrapper extends SimpleWrapper
{
    public Session session = SessionUtilsC.getMainSession();
    public Session session1;
    public String symbolName;
    public Symbol symbol;
    public Contract contract;
    protected static DateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd HH:mm:ss zzz" );
    protected Map<String, Symbol> symbolMap;
    protected static final TimeZone tz = TimeZone.getTimeZone( "IST" );
    protected Transaction tx = null;
    protected boolean isDataDone = false;
    protected boolean isDataError = false;
    protected final SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd hh:mm:ss" );

    static
    {
        dateFormat.setTimeZone( tz );
    }

    public void startTransaction()
    {
        tx = session1.beginTransaction();
    }

    public void commitTransaction()
    {
        if ( tx != null )
        {
            tx.commit();
        }

        startTransaction();
    }

    @Override
    public void connect( int connectID )
    {
        super.connect( connectID );
        if ( getSocket().isConnected() )
        {
            log.info( "Connected to Interactive Brokers." );
        }
    }

    @Override
    public void historicalData( int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps )
    {
        update( date, open, high, low, close, volume );
    }

    @Override
    public void error( int id, int errorCode, String errorMsg )
    {
        logIn( "Error id=" + id + " code=" + errorCode + " msg=" + errorMsg );
        if ( errorCode == 162 )
        {
            if ( errorMsg.contains( "Historical data request pacing violation" ) )
            {
                try
                {
                    log.info( "Sleeping for 10 mins due to IB restrictions" );
                    Thread.sleep( 61000 );
                }
                catch ( InterruptedException e )
                {
                    log.error( "Error : ", e );
                }
            }
            else
            {
                if ( symbol != null )
                {
                    log.info( "Error in updating data for Symbol : " + symbol.getName() );
                    commitTransaction();
                    isDataDone = true;
                    isDataError = true;
                }
                return;
            }
            isDataDone = true;
            return;
        }
        else if ( errorCode == 504 )
        {
            connect( 0 );
            isDataDone = true;
            return;
        }

        if ( symbol != null )
        {
            log.info( "Error in updating data for Symbol : " + symbol.getName() );
            commitTransaction();
            isDataDone = true;
            isDataError = true;
        }
    }

    public abstract void update( String date, double open, double high, double low, double close, int volume );

    public void setSymbol( Contract _contract )
    {
        contract = _contract;
        isDataDone = false;
        symbolMap = DownloaderUtilC.getSymbolMap( contract.m_exchange );
        String symbolName;
        if ( "IDEALPRO".equals( contract.m_exchange ) )
        {
            symbolName = contract.m_symbol + "/" + contract.m_currency;
        }
        else
        {
            symbolName = contract.m_symbol;
        }
        if ( !symbolMap.keySet().contains( symbolName ) )
        {
            Transaction tx = session.beginTransaction();
            symbol = new Symbol();
            symbol.setName( symbolName );
            symbol.setExchange( contract.m_exchange );
            session.save( symbol );
            tx.commit();
            symbolMap.put( symbolName, symbol );
        }
        else
        {
            symbol = symbolMap.get( symbolName );
        }
    }

    public abstract void reqHistoricalData( Date fromDate, Date toDate );

    public void waitTillOperationCompletes()
    {
        try
        {
            while ( !isDataDone )
            {
                Thread.sleep( 500 );
            }
            log.info( "Done Waiting......" );
        }
        catch ( InterruptedException e )
        {
            log.error( "Error : ", e );
        }
    }

    public static int getDaysDiff( Date fromDate, Date toDate )
    {
        Calendar fromDateCal = Calendar.getInstance();
        Calendar toDateCal = Calendar.getInstance();
        fromDateCal.setTime( fromDate );
        toDateCal.setTime( toDate );
        return ( int ) ( ( toDateCal.getTimeInMillis() - fromDateCal.getTimeInMillis() ) / ( 24 * 60 * 60 * 1000 ) ) + 1;

    }


}
