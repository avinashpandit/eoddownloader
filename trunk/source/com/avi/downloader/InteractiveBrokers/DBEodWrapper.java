package com.avi.downloader.InteractiveBrokers;

import com.avi.core.EndOfDay;
import com.avi.util.DownloaderUtilC;
import com.avi.util.SessionUtilsC;
import org.hibernate.Session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DBEodWrapper extends DBWrapper
{
    protected static DateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd" );

    public DBEodWrapper()
    {
        session1 = SessionUtilsC.getDailySession();
    }

    @Override
    public void update( String date, double open, double high, double low, double close, int volume )
    {
        try
        {
            boolean finished = false;
            EndOfDay holder = new EndOfDay();
            if ( date.startsWith( "finished" ) )
            {
                finished = true;
                log.info( "Update finished for Symbol : " + symbol.getName() );
                commitTransaction();
                isDataDone = true;
            }
            else
            {
                holder.setDate( dateFormat.parse( date ) );
                holder.setOpen( ( float ) open );
                holder.setHigh( ( float ) high );
                holder.setLow( ( float ) low );
                holder.setClose( ( float ) close );
                holder.setVolume( volume );
                holder.setSymbol( symbol.getId() );
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
        Session session = SessionUtilsC.getDailySession();
        int daysDiff = 0;
        int today = DownloaderUtilC.getFibboDateFromDate( new Date() );

        try
        {
            List eods = session.createQuery( "select max(tickDate) from EndOfDay where symbol = " + symbol.getId() ).list();
            int tickDate = ( Integer ) eods.get( 0 );
            daysDiff = today - tickDate;
        }
        catch ( Exception e )
        {
            daysDiff = 365;
        }

        if ( daysDiff < 1 )
        {
            log.info( "Symbol : " + symbol.getName() + " already has data for given Range." );
            return;
        }

        else
        {
            if ( daysDiff >= 365 )
            {
                getSocket().reqHistoricalData( 0, contract, sdf.format( new Date() ), "1 Y", "1 day", "TRADES", 1, 1 );
            }
            else
            {
                getSocket().reqHistoricalData( 0, contract, sdf.format( new Date() ), daysDiff + " D", "1 day", "TRADES", 1, 1 );
            }
            log.info( "Daydifff --- > " + daysDiff );
            waitTillOperationCompletes();
        }
    }

}
