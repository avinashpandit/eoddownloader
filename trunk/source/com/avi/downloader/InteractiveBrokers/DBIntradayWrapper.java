package com.avi.downloader.InteractiveBrokers;

import com.avi.core.IntraDay;
import com.avi.util.SessionUtilsC;
import org.hibernate.Session;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class DBIntradayWrapper extends DBWrapper
{

    String whatToShow = "TRADES";
    int useRTH = 1;
    int formatDate = 1;

    public DBIntradayWrapper( boolean isForex )
    {
        // TODO Auto-generated constructor stub
        session1 = SessionUtilsC.getIntradaySession();
        if ( isForex )
        {
            whatToShow = "MIDPOINT";
            useRTH = 1;
        }
    }

    @Override
    public void update( String date, double open, double high, double low, double close, int volume )
    {
        try
        {
            boolean finished = false;
            IntraDay holder = new IntraDay();
            if ( date.startsWith( "finished" ) )
            {
                finished = true;
                log.info( "Update finished for Symbol : " + symbol.getName() );
                commitTransaction();
                isDataDone = true;
            }
            else
            {
                holder.setTickDate( dateFormat.parse( date + " PST" ).getTime() );
                holder.setOpen( ( float ) open );
                holder.setHigh( ( float ) high );
                holder.setLow( ( float ) low );
                holder.setClose( ( float ) close );
                holder.setVolume( volume );
                holder.setSymbol( symbol.getId() );
                holder.setName( symbolName );
                session1.save( holder );
            }
        }
        catch ( Exception e )
        {
            log.error( "Error : ", e );
        }
    }

    @Override
    public void reqHistoricalData( Date fromDate, Date toDate )
    {
        isDataDone = false;

        int daysDiff = getDaysDiff( fromDate, toDate );
        log.info( "fromDate: " + fromDate + " ToDate : " + toDate );
        log.info( contract.m_symbol + "," + contract.m_secType + "," + contract.m_expiry + "," + contract.m_right + "," + contract.m_strike + "," + contract.m_multiplier + "," + contract.m_exchange + "," + contract.m_primaryExch + "," + contract.m_currency + "," + contract.m_localSymbol );
        if ( daysDiff <= 0 )
        {
            log.info( "Symbol : " + symbol.getName() + " already has data for given Range." );
            return;
        }

        else if ( daysDiff <= 5 )
        {
            getSocket().reqHistoricalData( 0, contract, sdf.format( toDate ), daysDiff + " D", "1 min", whatToShow, useRTH, formatDate );
            log.info( "Daydifff < 5 --- > " + daysDiff );
            waitTillOperationCompletes();
        }
        else
        {
            getSocket().reqHistoricalData( 0, contract, sdf.format( toDate ), "5 D", "1 min", whatToShow, useRTH, formatDate );
            waitTillOperationCompletes();
            if ( !isDataError )
            {
                Session session = SessionUtilsC.getIntradaySession();
                List list = session.createQuery( "select min(tickDate) from IntraDay where symbol = " + symbol.getId() ).list();
                Date newDate = null;
                try
                {
                    newDate = new Date( ( ( Timestamp ) list.get( 0 ) ).getTime() );
                }
                catch ( Exception e )
                {
                    newDate = toDate;
                }
                reqHistoricalData( fromDate, newDate );
            }
            else
            {
                isDataError = false;
            }
        }
    }


}
