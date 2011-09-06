package com.avi.downloader.google;

import au.com.bytecode.opencsv.CSVReader;
import com.avi.core.EndOfDay;
import com.avi.core.Symbol;
import com.avi.properties.PropertyMBeanC;
import com.avi.util.DownloaderUtilC;
import com.avi.util.SessionUtilsC;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EndOfDayGoogleDownloaderC
{
    protected Logger log = LoggerFactory.getLogger( this.getClass() );
    public static String folderName = PropertyMBeanC.getInstance().getEODDataFolder();

    public Map<String, Symbol> symbolMap = new HashMap<String, Symbol>();
    public List<Symbol> symbolList;
    static SimpleDateFormat sf = new SimpleDateFormat( "MM/dd/yyyy" );

    public void clearFolder()
    {
        File aDirectory = new File( folderName );
        log.info( "Avinash" + folderName );
        File[] afiles = aDirectory.listFiles();
        for ( File aFile : afiles )
        {
            aFile.delete();
        }
    }

    public Symbol createSymbol( String name, String exchange )
    {
        Symbol symbol;
        Session session = SessionUtilsC.getMainSession();
        Transaction tx = session.beginTransaction();
        symbol = new Symbol();
        symbol.setName( name );
        symbol.setExchange( exchange );
        session.save( symbol );
        log.info( "Creating new Symbol : " + symbol.getName() );
        tx.commit();
        session.close();
        return symbol;
    }

    public void uploadFileToDB( FileReader fileReader, String symbolName, String exchange ) throws Exception
    {
        CSVReader reader = new CSVReader( fileReader );
        List<String[]> myEntries = reader.readAll();
        // int i = 732300;
        Symbol symbol;
        if ( !symbolMap.keySet().contains( symbolName ) )
        {
            symbol = createSymbol( symbolName, exchange );
            symbolMap.put( symbolName, symbol );
        }
        else
        {
            symbol = symbolMap.get( symbolName );
            if ( !exchange.equals( symbol.getExchange() ) )
            {
                log.info( "##############Symbol : " + symbolName + " already present in different exchange, skipping download." );
                return;
            }
        }

        log.info( "Loading data for symbol : " + symbolName );
        Session session2 = SessionUtilsC.getDailySession();
        try
        {
            Transaction tx1 = session2.beginTransaction();

            for ( String[] temp : myEntries )
            {
                if ( temp[0].trim().indexOf( "Date" ) > -1 )
                {
                    continue;
                }
                else
                {
                    // Transaction tx1 = session1.beginTransaction();
                    float volume = Long.parseLong( temp[5] );
                    if ( volume > 0 )
                    {
                        EndOfDay eod = new EndOfDay();
                        eod.setClose( Float.parseFloat( temp[4] ) );
                        eod.setLow( Float.parseFloat( temp[3] ) );
                        eod.setOpen( Float.parseFloat( temp[1] ) );
                        eod.setSymbol( symbol.getId() );
                        eod.setHigh( Float.parseFloat( temp[2] ) );
                        eod.setDate( new Date( temp[0] ) );
                        eod.setVolume( volume );
                        session2.save( eod );

                        /*               Session session = SessionUtilsC.getMainSession();
                                       Transaction tx = session.beginTransaction();
                                       symbol.setAvgVolume( volume );
                                       session.update(  symbol );
                                       tx.commit();
                                       session.close();
                        */
                    }
                }
            }
            tx1.commit();
        }
        catch ( Exception e )
        {
            System.out.print( "Unable to write to the DB " );
        }
        session2.close();

    }

    protected abstract void download();


    public Date getLatestDataDate( Symbol symbol )
    {
        int tickDate = 0;
        Session session1 = SessionUtilsC.getDailySession();

        try
        {
            ScrollableResults eods = session1.createQuery( "select max(s.tickDate) from EndOfDay s where s.symbol = " + symbol.getId() ).scroll();
            eods.setRowNumber( 0 );
            tickDate = eods.getInteger( 0 );
        }
        catch ( Exception e )
        {
            //log.error("Error : " , e);
        }

        session1.close();

        if ( tickDate == 0 )
        {
            return new Date( "01/01/2008" );
        }
        return DownloaderUtilC.getFibboDateFromLong( tickDate + 1 );
    }

    public String getLatestDataDateStr( Symbol symbol )
    {
        synchronized ( sf )
        {
            return sf.format( getLatestDataDate( symbol ) );
        }
    }

}
