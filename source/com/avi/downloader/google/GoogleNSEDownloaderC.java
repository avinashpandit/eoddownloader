package com.avi.downloader.google;

import com.avi.core.Symbol;
import com.avi.downloader.google.filter.ContractTableFilter;
import com.avi.persistence.HibernateUtil;
import com.avi.properties.PropertyMBeanC;
import com.avi.util.CallerRunPolicy;
import com.avi.util.DownloaderUtilC;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GoogleNSEDownloaderC extends EndOfDayGoogleDownloaderC
{
    Parser parser;
    static ContractTableFilter filter = new ContractTableFilter();
    static SimpleDateFormat sf1 = new SimpleDateFormat( "dd-MMM-YY" );
    static String folderName = PropertyMBeanC.getInstance().getEODDataFolder();
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor( 1, 1, 1L, TimeUnit.SECONDS, new SynchronousQueue() );
    public static CallerRunPolicy caller = new CallerRunPolicy();
    static Date todayDate = new Date();

    static
    {
        executor.setRejectedExecutionHandler( caller );
    }


    @Override
    public void download()
    {
        clearFolder();
        //deleteQuoteData("NSE");

        Collection<Symbol> symbolList = DownloaderUtilC.getSymbolList( "NSE" );

        for ( Symbol symbol : symbolList )
        {
            symbolMap.put( symbol.getName(), symbol );
        }

        try
        {
            for ( Symbol symbol : symbolList )
            {
                GoogleNSEDownloaderRunner runner = new GoogleNSEDownloaderRunner( symbol );
                //runner.run();
                executor.execute( runner );
            }

        }
        catch ( Exception e )
        {
            log.error( "Error : ", e );
        }

    }

    /**
     * The main program, which can be executed from the command line.
     *
     * @param args A URL or file name to parse, and an optional tag name to be
     *             used as a filter.
     */
    public static void main( String[] args )
    {
        GoogleNSEDownloaderC downloader = new GoogleNSEDownloaderC();
        downloader.download();
        DownloaderUtilC.fixCorruptedEODData();
        HibernateUtil.shutdown();
        System.exit( -1 );
    }

    private class GoogleNSEDownloaderRunner implements Runnable
    {

        public GoogleNSEDownloaderRunner( Symbol _symbol )
        {
            symbol = _symbol;
        }

        public Symbol symbol;

        public void run()
        {

            String file = folderName + '\\' + symbol.getName() + ".html";
            String csvFile = folderName + '\\' + symbol.getName() + ".csv";

            download( symbol, csvFile, file );
            try
            {
                FileReader reader = new FileReader( csvFile );
                uploadFileToDB( reader, symbol.getName(), "NSE" );
                reader.close();
                File file1 = new File( csvFile );
                file1.delete();
            }
            catch ( Exception e )
            {
                log.error( "Unable to get file and write to DB for symbol : " + symbol );
            }
        }

        public void download( Symbol symbol, String csvFile, String file )
        {

            File child = new File( file );
            File csvChild = new File( csvFile );

            try
            {
                log.info( "Downloading : " + symbol );
                Date startDate = getLatestDataDate( symbol );
                long todayLong = DownloaderUtilC.getFibboDateFromDate( todayDate );
                long startDateLong = DownloaderUtilC.getFibboDateFromDate( startDate );
                String startDateStr = null;
                synchronized ( sf )
                {
                    startDateStr = sf.format( startDate );
                }

                int itrTimes = ( ( int ) ( ( todayLong - startDateLong ) * 1.0 / 200.0 ) ) + 1;
                //log.info( itrTimes + " "  + startDateLong + " "  + todayLong );
                BufferedWriter out = new BufferedWriter( new FileWriter( csvFile ) );
                out.write( "Date,Open,High,Low,Close,Volume\n" );
                for ( int i2 = 0; i2 < itrTimes; i2++ )
                {
                    DownloaderUtilC.download( PropertyMBeanC.getInstance().getGoogleNSEAddress() + symbol.getName() + "&startdate=" + startDateStr + "&start=" + i2 * 200 + "&num=200", file, "SC=RV=4522105-4112-207437-6010110-983582-694653-7135704-5820065-2108958-11681293:ED=us; PREF=ID=0f8aae0e7f754ea6:U=feb6182f324d4e04:FF=4:LD=en:NR=10:TM=1269385699:LM=1269585295:DV=8Z2250Ah6zcH:GM=1:IG=3:S=BOlPdNDo9HWEclKS; NID=33=IEMNyQOQ_oX4MiGbZzPFyTJZCs5ud_oFvCi28514mDS-7zjyCidaffZaN2FRm-ScetEsUajQAivG9CEJl3nSCVwBVxUpztsljvD5rIHERdqmb0E3pCflY-y4tvUdQNn4; SID=DQAAAJcAAAApDirLmIdYUnUE1ZryT41HqMoeKXL-iwqGpbaF9OWay1qyYmH-sW1c9zMuPjetnAVL8zq6Cc8hWnmOrler_15kkoojyK2YtyMUK_S-gOFg4APw3POecSJGudEJK4xEXwpLkKgfZj4aOt-qMylUxQECWgy6dBv3iP-bJXPUY___f8-7qs1P3Qk73xXlcUiSgLip859gSeYGp2gX-SrfXfA0; HSID=AZGDYgaWApSjWQp40; TZ=420; GDSESS=ID=0f8aae0e7f754ea6:EX=1270664482:S=6ogolurNKNsj_6Ec; S=sorry=SuA7mFDf7IUZxRiols7GtA:quotestreamer=WNokNumS1WG2bwPwnMHYig" );

                    parser = new Parser();
                    parser.setResource( child.getPath() );
                    NodeList nodeList = parser.parse( filter );
                    if ( nodeList.size() == 0 )
                    {
                        log.info( "Can not parse : " + child.getPath() );
                        break;
                    }
                    for ( int i = 0; i < nodeList.size(); i++ )
                    {
                        Node node = nodeList.elementAt( i );
                        NodeList childNodeList = node.getChildren();
                        for ( int i1 = 0; i1 < childNodeList.size(); i1++ )
                        {
                            try
                            {
                                Node childeNode = childNodeList.elementAt( i1 );
                                NodeList nodeList1 = childeNode.getChildren().elementAt( 7 ).getChildren();
                                while ( nodeList1 != null )
                                {
                                    //System.out.print(nodeList1.elementAt( 1 ).toPlainTextString());
                                    String dateStr = nodeList1.elementAt( 1 ).toPlainTextString().replace( '\n', ' ' );
                                    Date date = new Date( dateStr );
                                    //System.out.print(date);
                                    float open = getNumberFrmString( nodeList1.elementAt( 2 ).toPlainTextString() );
                                    float high = getNumberFrmString( nodeList1.elementAt( 3 ).toPlainTextString() );
                                    float low = getNumberFrmString( nodeList1.elementAt( 4 ).toPlainTextString() );
                                    float close = getNumberFrmString( nodeList1.elementAt( 5 ).toPlainTextString() );
                                    long volume = getLongFrmString( nodeList1.elementAt( 6 ).toPlainTextString() );
                                    out.write( "\"" + dateStr + "\"," + open + ',' + high + ',' + low + ',' + close + ',' + volume + '\n' );
                                    nodeList1 = nodeList1.elementAt( 7 ).getChildren();
                                }
                            }
                            catch ( NullPointerException e )
                            {
                                //to escape first and last child
                            }
                        }
                    }
                }
                out.close();
                child.delete();
            }
            catch ( ParserException e )
            {
                log.info( child.getPath() );
                log.error( "Error : ", e );
            }
            catch ( IOException e )
            {
                log.error( "Error : ", e );
            }
            catch ( Exception e )
            {
                log.error( "Error : ", e );
            }

        }

        public float getNumberFrmString( String str )
        {
            return Float.parseFloat( str.replace( '\n', ' ' ).replaceAll( ",", "" ).trim() );
        }

        public long getLongFrmString( String str )
        {
            return Long.parseLong( str.replace( '\n', ' ' ).replaceAll( ",", "" ).trim() );
        }
    }

}
