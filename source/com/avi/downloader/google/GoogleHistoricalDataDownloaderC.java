package com.avi.downloader.google;

import com.avi.core.Symbol;
import com.avi.persistence.HibernateUtil;
import com.avi.properties.PropertyMBeanC;
import com.avi.util.CallerRunPolicy;
import com.avi.util.DownloaderUtilC;
import org.hibernate.Session;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: avinash
 * Date: Oct 3, 2008
 * Time: 4:18:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoogleHistoricalDataDownloaderC extends EndOfDayGoogleDownloaderC
{

    public String googleURL = PropertyMBeanC.getInstance().getGoogleCSVAddress();
    public String folderName = PropertyMBeanC.getInstance().getEODDataFolder();
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor( 2, 4, 1L, TimeUnit.SECONDS, new SynchronousQueue() );
    public static CallerRunPolicy caller = new CallerRunPolicy();

    static
    {
        executor.setRejectedExecutionHandler( caller );
    }

    private final String[] symbolName = {"MMM", "ABT", "ANF", "ACE", "ADBE", "AMD", "AES", "AET", "ACS", "AFL", "A", "APD", "AKAM", "AA", "AYE", "ATI", "AGN", "ALL", "ALTR", "MO", "AMZN", "ABK", "AEE", "ACAS", "AEP", "AXP", "AIG", "ASD", "AMT", "AMP", "ABC", "AMGN", "APC", "ADI", "BUD", "AOC", "APA", "AIV", "APOL", "AAPL", "ABI", "AMAT", "ADM", "ASH", "AIZ", "T", "ADSK", "ADP", "AN", "AZO", "AVB", "AVY", "AVP", "BHI", "BLL", "BAC", "BK", "BCR", "BRL", "BAX", "BBT", "BSC", "BDX", "BBBY", "BMS", "BBY", "BIG", "BIIB", "BJS", "BDK", "HRB", "BMC", "BA", "BXP", "BSX", "BMY", "BRCM", "BF.B", "BC", "BNI", "CHRW", "CA", "CPB", "COF", "CAH", "CCL", "CAT", "CBG", "CBS", "CELG", "CNP", "CTX", "CTL", "SCHW", "CHK", "CVX", "CB", "CIEN", "CI", "CINF", "CTAS", "CC", "CSCO", "C", "CZN", "CTXS", "CCU", "CLX", "CME", "CMS", "COH", "KO", "CCE", "CTSH", "CL", "CMCSA", "CMA", "CBH", "CSC", "CPWR", "CAG", "COP", "CNX", "ED", "STZ", "CEG", "CVG", "CBE", "GLW", "COST", "CFC", "CVH", "COV", "CSX", "CMI", "CVS", "DHI", "DHR", "DRI", "DF", "DE", "DELL", "DDR", "DVN", "DDS", "DTV", "DFS", "D", "RRD", "DOV", "DOW", "DJ", "DTE", "DD", "DUK", "DYN", "ETFC", "EMN", "EK", "ETN", "EBAY", "ECL", "EIX", "EP", "ERTS", "EDS", "EQ", "EMC", "EMR", "ESV", "ETR", "EOG", "EFX", "EQR", "EL", "EXC", "EXPE", "EXPD", "ESRX", "XOM", "FDO", "FNM", "FRE", "FII", "FDX", "FIS", "FITB", "FHN", "FE", "FISV", "FLR", "F", "FRX", "FO", "FPL", "BEN", "FCX", "GCI", "GPS", "GD", "GE", "GIS", "GM", "GPC", "GNW", "GENZ", "GILD", "GS", "GR", "GT", "GOOG", "GWW", "HAL", "HOG", "HAR", "HET", "HIG", "HAS", "HNZ", "HPC", "HES", "HPQ", "HD", "HON", "HSP", "HST", "HCBK", "HUM", "HBAN", "IACI", "ITW", "RX", "IR", "TEG", "INTC", "ICE", "IBM", "IFF", "IGT", "IP", "IPG", "INTU", "ITT", "JBL", "JEC", "JNS", "JDSU", "JNJ", "JCI", "JNY", "JPM", "JNPR", "KBH", "K", "KEY", "KMB", "KIM", "KG", "KLAC", "KSS", "KFT", "KR", "LLL", "LH", "LM", "LEG", "LEH", "LEN", "LUK", "LXK", "LLY", "LTD", "LNC", "LLTC", "LIZ", "LMT", "LTR", "LOW", "LSI", "MTB", "M", "MTW", "MRO", "MAR", "MMC", "MI", "MAS", "MAT", "MBI", "MKC", "MCD", "MHP", "MCK", "MWV", "MHS", "MDT", "WFR", "MRK", "MDP", "MER", "MET", "MTG", "MCHP", "MU", "MSFT", "MIL", "MOLX", "TAP", "MON", "MNST", "MCO", "MS", "MOT", "MUR", "MYL", "NBR", "NCC", "NOV", "NSM", "NTAP", "NYT", "NWL", "NEM", "NWS.A", "GAS", "NKE", "NI", "NE", "NBL", "JWN", "NSC", "NTRS", "NOC", "NOVL", "NVLS", "NUE", "NVDA", "NYX", "OXY", "ODP", "OMX", "OMC", "ORCL", "PCAR", "PTV", "PLL", "PH", "PDCO", "PAYX", "BTU", "JCP", "POM", "PBG", "PEP", "PKI", "PFE", "PCG", "PNW", "PBI", "PCL", "PNC", "RL", "PPG", "PPL", "PX", "PCP", "PFG", "PG", "PGN", "PGR", "PLD", "PRU", "PEG", "PSA", "PHM", "QLGC", "QCOM", "DGX", "STR", "Q", "RSH", "RTN", "RF", "RAI", "RHI", "ROK", "COL", "ROH", "RDC", "R", "SAF", "SWY", "SNDK", "SLE", "SGP", "SLB", "SSP", "SEE", "SHLD", "SRE", "SHW", "SIAL", "SPG", "SLM", "SII", "SNA", "SO", "LUV", "SOV", "SE", "S", "STJ", "SWK", "SPLS", "SBUX", "HOT", "STT", "SYK", "JAVAD", "SUN", "STI", "SVU", "SYMC", "SNV", "SYY", "TROW", "TGT", "TE", "TLAB", "TIN", "THC", "TDC", "TER", "TEX", "TSO", "TXN", "TXT", "HSY", "TRV", "TMO", "TIF", "TWX", "TIE", "TJX", "TMK", "RIG", "TRB", "TEL", "TYC", "TSN", "USB", "UNP", "UIS", "UNH", "UPS", "X", "UTX", "UNM", "UST", "VFC", "VLO", "VAR", "VRSN", "VZ", "VIA.B", "VNO", "VMC", "WB", "WMT", "WAG", "DIS", "WM", "WMI", "WAT", "WPI", "WFT", "WLP", "WFC", "WEN", "WU", "WY", "WHR", "WFMI", "WMB", "WIN", "WWY", "WYE", "WYN", "XEL", "XRX", "XLNX", "XL", "XTO", "YHOO", "YUM", "ZMH", "ZION", "SPY"};

    @Override
    public void download()
    {
        clearFolder();
        //deleteQuoteData( "SMART" );

        for ( Symbol symbol : DownloaderUtilC.getSymbolList( "SMART" ) )
        {
            symbolMap.put( symbol.getName(), symbol );
        }

        if ( symbolMap.size() == 0 )
        {
            for ( String symbolStr : symbolName )
            {
                Symbol symbol = createSymbol( symbolStr, "SMART" );
                symbolMap.put( symbolStr, symbol );
            }
        }

        try
        {
            for ( Symbol symbol : symbolMap.values() )
            {
                GoogleHistoricalDataDownloaderRunner runner = new GoogleHistoricalDataDownloaderRunner( symbol );
                executor.execute( runner );
            }

        }
        catch ( Exception e )
        {
            log.error( "Error : ", e );
        }

    }

    private class GoogleHistoricalDataDownloaderRunner implements Runnable
    {
        private Symbol symbol;
        private Session session1;

        public GoogleHistoricalDataDownloaderRunner( Symbol _symbol )
        {
            symbol = _symbol;
        }

        public void download()
        {
            StringBuilder url = new StringBuilder( 100 );
            String footerURL = "&startdate=" + getLatestDataDateStr( symbol ) + "&output=csv";

            url.append( googleURL ).append( symbol.getName() ).append( footerURL );
            log.info( "Downloading : " + url );
            String fileName = folderName + symbol.getName() + ".csv";
            download( url.toString(), fileName );
            try
            {
                FileReader reader = new FileReader( fileName );
                uploadFileToDB( reader, symbol.getName(), "SMART" );
                reader.close();
                File file = new File( fileName );
                file.delete();
            }
            catch ( Exception e )
            {
                log.info( "Unable to get file and write to DB for symbol : " + symbol.getName() );
            }
        }

        private void download( String address, String localFileName )
        {
            OutputStream out = null;
            URLConnection conn = null;
            InputStream in = null;
            try
            {
                URL url = new URL( address );
                conn = url.openConnection();
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

        public void run()
        {
            download();
        }
    }

    public static void main( String[] args )
    {
        GoogleHistoricalDataDownloaderC downloader = new GoogleHistoricalDataDownloaderC();
        downloader.download();

        //fix any corrupted data
        DownloaderUtilC.fixCorruptedEODData();
        // Shutting down the application
        HibernateUtil.shutdown();
    }

}
