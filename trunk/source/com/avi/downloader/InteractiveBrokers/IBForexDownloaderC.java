package com.avi.downloader.InteractiveBrokers;

import com.avi.util.SessionUtilsC;
import com.ib.client.Contract;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IBForexDownloaderC
{
    protected static Logger log = LoggerFactory.getLogger( IBForexDownloaderC.class );

    public static void main( String[] args )
    {
        try
        {
            DBIntradayWrapper wrapper = new DBIntradayWrapper( true );

            wrapper.connect( 0 );

            SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd hh:mm:ss" );

            log.info( sdf.format( new Date() ) );
            //String[] symbolName = { "EUR/USD","USD/JPY","GBP/USD","USD/CHF","USD/CAD","NZD/USD","GBP/JPY","EUR/CHF","EUR/JPY","EUR/GBP","GBP/CHF","EUR/AUD","CAD/JPY","CHF/JPY","AUD/JPY","AUD/USD","AUD/NZD","EUR/CAD","EUR/NOK","EUR/SEK","USD/NOK","USD/SEK"};
            String[] symbolName = {"EUR/USD", "GBP/USD", "USD/CHF", "USD/CAD", "NZD/USD", "EUR/CHF", "EUR/GBP", "GBP/CHF", "EUR/AUD", "AUD/USD", "AUD/NZD", "EUR/CAD"};
            for ( String symbol : symbolName )
            {
                Contract contract = getContract( symbol );
                wrapper.setSymbol( contract );
                wrapper.startTransaction();
                //wrapper.getSocket().reqHistoricalData( 0, contract, sdf.format( new Date() ), "5 D", "1 min", "TRADES", 1, 1 );
                Session session = SessionUtilsC.getIntradaySession();
                List list = session.createQuery( "select max(tickDate) from IntraDay where symbol = " + wrapper.symbol.getId() ).list();
                Date newDate = null;
                try
                {
                    newDate = new Date( ( ( Timestamp ) list.get( 0 ) ).getTime() );
                }
                catch ( Exception e )
                {
                    newDate = new Date( "3-DEC-2009" );
                }

                wrapper.reqHistoricalData( newDate, new Date() );
            }

            wrapper.disconnect();
        }
        catch ( Exception e )
        {
            log.error( "Error : ", e );
        }
    }

    public static Contract getContract( String symbolName )
    {
        Contract contract = new Contract();
        String[] ccys = symbolName.split( "/" );
        contract.m_symbol = ccys[0];
        contract.m_secType = "CASH";
        contract.m_expiry = "";
        contract.m_right = "";
        contract.m_strike = 0;
        contract.m_multiplier = "";
        contract.m_exchange = "IDEALPRO";
        contract.m_primaryExch = "IDEALPRO";
        contract.m_currency = ccys[1];
        contract.m_localSymbol = "";

        return contract;
    }
}
