package com.avi.util;

import com.avi.core.EndOfDay;
import com.avi.core.Symbol;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloaderUtilC
{
    static GregorianCalendar day;
    protected static Logger log = LoggerFactory.getLogger( DownloaderUtilC.class );

    static
    {
        day = new GregorianCalendar();
        day.setTime( new Date( "19-OCT-2008" ) );
    }

    public static int getFibboDateFromDate( Date date )
    {
        GregorianCalendar gDate = new GregorianCalendar();
        gDate.setTime( date );
        return ( int ) ( ( gDate.getTimeInMillis() - day.getTimeInMillis() ) / ( 24 * 60 * 60 * 1000 ) ) + 733700;
    }

    public static Date getFibboDateFromLong( long longDate )
    {
        GregorianCalendar gDate = new GregorianCalendar();
        long timeInMillis = ( longDate - 733700 ) * ( ( 24 * 60 * 60 * 1000 ) ) + day.getTimeInMillis();
        gDate.setTimeInMillis( timeInMillis );
        return gDate.getTime();
    }

    public static Map<String, Symbol> getSymbolMap( String exchange )
    {
        Map<String, Symbol> symbolMap = new HashMap();
        for ( Symbol symbol : getSymbolList( exchange ) )
        {
            symbolMap.put( symbol.getName(), symbol );
        }

        return symbolMap;
    }

    public static List<Symbol> getSymbolList( String exchange )
    {
        List<Symbol> symbolList;
        Session session = SessionUtilsC.getMainSession();
        Transaction tx = session.beginTransaction();

        if ( exchange == null || "".equals( exchange ) )
        {
            symbolList = session.createQuery( "from Symbol s" ).list();
        }
        else
        {
            symbolList = session.createQuery( "from Symbol s where s.exchange = '" + exchange + "'" ).list();
        }
        tx.commit();
        session.close();
        return symbolList;
    }

    public static Map<String, Symbol> getSymbolMap()
    {
        return getSymbolMap( null );
    }

    public static void download( String address, String localFileName )
    {
        download( address, localFileName, null );
    }

    public static void download( String address, String localFileName, String cookie )
    {
        log.info( "Downloading : " + address );
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;
        try
        {
            URL url = new URL( address );
            conn = url.openConnection();
            if ( cookie != null )
            {
                conn.addRequestProperty( "Cookie", cookie );
            }

            conn.addRequestProperty( "User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/533.2 (KHTML, like Gecko) Chrome/5.0.342.8 Safari/533.2" );
            in = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int numRead;
            long numWritten = 0;
            out = new BufferedOutputStream( new FileOutputStream( localFileName ) );

            while ( ( numRead = in.read( buffer ) ) != -1 )
            {
                out.write( buffer, 0, numRead );
                numWritten += numRead;
            }
            log.info( localFileName + '\t' + numWritten );
        }
        catch ( Exception exception )
        {
            log.info( "Could not Download the file " + localFileName );
            exception.printStackTrace();
        }
        finally
        {
            try
            {
                if ( in != null )
                {
                    in.close();
                }
                if ( out != null )
                {
                    out.close();
                }
            }
            catch ( IOException ioe )
            {
            }
        }

    }

    public static void getZipFiles( String filename, String destinationFolder )
    {
        try
        {
            byte[] buf = new byte[1024];
            ZipInputStream zipinputstream = null;
            ZipEntry zipentry;
            zipinputstream = new ZipInputStream( new FileInputStream( filename ) );

            zipentry = zipinputstream.getNextEntry();
            while ( zipentry != null )
            {
                //for each entry to be extracted
                String entryName = zipentry.getName();
                log.info( "entryname " + entryName );
                int n;
                FileOutputStream fileoutputstream;
                File newFile = new File( entryName );
                String directory = newFile.getParent();

                if ( directory == null )
                {
                    if ( newFile.isDirectory() )
                    {
                        break;
                    }
                }
                File newExtractFile = new File( destinationFolder + entryName );
                if ( !newExtractFile.exists() )
                {
                    File dir = new File( newExtractFile.getParent() );
                    if ( !dir.exists() )
                    {
                        dir.mkdir();
                    }
                    newExtractFile.createNewFile();
                }
                fileoutputstream = new FileOutputStream( newExtractFile );

                while ( ( n = zipinputstream.read( buf, 0, 1024 ) ) > -1 )
                {
                    fileoutputstream.write( buf, 0, n );
                }

                fileoutputstream.close();
                zipinputstream.closeEntry();
                zipentry = zipinputstream.getNextEntry();

            }//while

            zipinputstream.close();
        }
        catch ( Exception e )
        {
            //no need to out anything
        }
    }

    public static void fixCorruptedEODData()
    {
        Session session1 = SessionUtilsC.getDailySession();
        Transaction tx = session1.beginTransaction();

        List<EndOfDay> eodList = session1.createQuery( "from EndOfDay s where s.open < s.low OR s.close < s.low " ).list();
        for ( EndOfDay eod : eodList )
        {
            if ( eod.getClose() < eod.getLow() )
            {
                eod.setClose( eod.getLow() );
                log.info( "Updating EOD : " + eod + " Reason : Close price less than Low." );
                session1.save( eod );
            }
            if ( eod.getOpen() < eod.getLow() )
            {
                eod.setOpen( eod.getLow() );
                log.info( "Updating EOD : " + eod + " Reason : Open price less than Low." );
                session1.save( eod );
            }
        }

        eodList = session1.createQuery( "from EndOfDay s where s.open > s.high OR s.close > s.high" ).list();
        for ( EndOfDay eod : eodList )
        {
            if ( eod.getClose() > eod.getHigh() )
            {
                eod.setClose( eod.getHigh() );
                log.info( "Updating EOD : " + eod + " Reason : Close price greater than High." );
                session1.save( eod );
            }
            if ( eod.getOpen() > eod.getHigh() )
            {
                eod.setOpen( eod.getHigh() );
                log.info( "Updating EOD : " + eod + " Reason : Open price greater than High." );
                session1.save( eod );
            }
        }

        StringBuilder hqlAppender = new StringBuilder();
        hqlAppender.append( "delete from EndOfDay where open = 0 OR close = 0 OR high = 0 OR low = 0" );
        tx = session1.beginTransaction();

        try
        {
            Query query = session1.createQuery( hqlAppender.toString() );
            int row = query.executeUpdate();
            tx.commit();
        }
        catch ( Exception e )
        {
            log.error( "Error : ", e );
        }

        session1.close();
    }

    public static void deleteQuoteData( String exchange )
    {
        Session session1 = SessionUtilsC.getDailySession();
        StringBuilder hqlAppender = new StringBuilder();
        hqlAppender.append( "delete from EndOfDay where symbol in ( " );
        for ( Symbol symbol : DownloaderUtilC.getSymbolList( null ) )
        {
            hqlAppender.append( symbol.getId() ).append( ',' );
        }
        hqlAppender.deleteCharAt( hqlAppender.length() - 1 );
        hqlAppender.append( " )" );
        Transaction tx = session1.beginTransaction();

        try
        {
            Query query = session1.createQuery( hqlAppender.toString() );
            int row = query.executeUpdate();
            tx.commit();
        }
        catch ( Exception e )
        {
            log.error( "Error : ", e );
        }

        session1.close();
    }

}