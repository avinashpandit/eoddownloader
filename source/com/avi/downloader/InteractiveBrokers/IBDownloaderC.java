package com.avi.downloader.InteractiveBrokers;

import com.ib.client.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IBDownloaderC
{
    protected static Logger log = LoggerFactory.getLogger( IBDownloaderC.class );

    public static void main( String[] args )
    {
        try
        {
            DBEodWrapper wrapper = new DBEodWrapper();

            wrapper.connect( 0 );

            SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd hh:mm:ss" );

            log.info( sdf.format( new Date() ) );
            //String[] symbolName = { "EDSL", "GVKP", "TCS", "RCOM", "BHARTI", "SACV", "IVRC", "UT", "GHFC", "INFO", "STLT" };

            String[] symbolName = {"MMM", "ABT", "ANF", "ACE", "ADBE", "AMD", "AES", "AET", "ACS", "AFL", "A", "APD", "AKAM", "AA", "AYE", "ATI", "AGN", "ALL", "ALTR", "MO", "AMZN", "ABK", "AEE", "ACAS", "AEP", "AXP", "AIG", "ASD", "AMT", "AMP", "ABC", "AMGN", "APC", "ADI", "BUD", "AOC", "APA", "AIV", "APOL", "AAPL", "ABI", "AMAT", "ADM", "ASH", "AIZ", "T", "ADSK", "ADP", "AN", "AZO", "AVB", "AVY", "AVP", "BHI", "BLL", "BAC", "BK", "BCR", "BRL", "BAX", "BBT", "BSC", "BDX", "BBBY", "BMS", "BBY", "BIG", "BIIB", "BJS", "BDK", "HRB", "BMC", "BA", "BXP", "BSX", "BMY", "BRCM", "BF.B", "BC", "BNI", "CHRW", "CA", "CPB", "COF", "CAH", "CCL", "CAT", "CBG", "CBS", "CELG", "CNP", "CTX", "CTL", "SCHW", "CHK", "CVX", "CB", "CIEN", "CI", "CINF", "CTAS", "CC", "CSCO", "CIT", "C", "CZN", "CTXS", "CCU", "CLX", "CME", "CMS", "COH", "KO", "CCE", "CTSH", "CL", "CMCSA", "CMA", "CBH", "CSC", "CPWR", "CAG", "COP", "CNX", "ED", "STZ", "CEG", "CVG", "CBE", "GLW", "COST", "CFC", "CVH", "COV", "CSX", "CMI", "CVS", "DHI", "DHR", "DRI", "DF", "DE", "DELL", "DDR", "DVN", "DDS", "DTV", "DFS", "D", "RRD", "DOV", "DOW", "DJ", "DTE", "DD", "DUK", "DYN", "ETFC", "EMN", "EK", "ETN", "EBAY", "ECL", "EIX", "EP", "ERTS", "EDS", "EQ", "EMC", "EMR", "ESV", "ETR", "EOG", "EFX", "EQR", "EL", "EXC", "EXPE", "EXPD", "ESRX", "XOM", "FDO", "FNM", "FRE", "FII", "FDX", "FIS", "FITB", "FHN", "FE", "FISV", "FLR", "F", "FRX", "FO", "FPL", "BEN", "FCX", "GCI", "GPS", "GD", "GE", "GIS", "GM", "GGP", "GPC", "GNW", "GENZ", "GILD", "GS", "GR", "GT", "GOOG", "GWW", "HAL", "HOG", "HAR", "HET", "HIG", "HAS", "HNZ", "HPC", "HES", "HPQ", "HD", "HON", "HSP", "HST", "HCBK", "HUM", "HBAN", "IACI", "ITW", "RX", "IR", "TEG", "INTC", "ICE", "IBM", "IFF", "IGT", "IP", "IPG", "INTU", "ITT", "JBL", "JEC", "JNS", "JDSU", "JNJ", "JCI", "JNY", "JPM", "JNPR", "KBH", "K", "KEY", "KMB", "KIM", "KG", "KLAC", "KSS", "KFT", "KR", "LLL", "LH", "LM", "LEG", "LEH", "LEN", "LUK", "LXK", "LLY", "LTD", "LNC", "LLTC", "LIZ", "LMT", "LTR", "LOW", "LSI", "MTB", "M", "MTW", "MRO", "MAR", "MMC", "MI", "MAS", "MAT", "MBI", "MKC", "MCD", "MHP", "MCK", "MWV", "MHS", "MDT", "WFR", "MRK", "MDP", "MER", "MET", "MTG", "MCHP", "MU", "MSFT", "MIL", "MOLX", "TAP", "MON", "MNST", "MCO", "MS", "MOT", "MUR", "MYL", "NBR", "NCC", "NOV", "NSM", "NTAP", "NYT", "NWL", "NEM", "NWS.A", "GAS", "NKE", "NI", "NE", "NBL", "JWN", "NSC", "NTRS", "NOC", "NOVL", "NVLS", "NUE", "NVDA", "NYX", "OXY", "ODP", "OMX", "OMC", "ORCL", "PCAR", "PTV", "PLL", "PH", "PDCO", "PAYX", "BTU", "JCP", "POM", "PBG", "PEP", "PKI", "PFE", "PCG", "PNW", "PBI", "PCL", "PNC", "RL", "PPG", "PPL", "PX", "PCP", "PFG", "PG", "PGN", "PGR", "PLD", "PRU", "PEG", "PSA", "PHM", "QLGC", "QCOM", "DGX", "STR", "Q", "RSH", "RTN", "RF", "RAI", "RHI", "ROK", "COL", "ROH", "RDC", "R", "SAF", "SWY", "SNDK", "SLE", "SGP", "SLB", "SSP", "SEE", "SHLD", "SRE", "SHW", "SIAL", "SPG", "SLM", "SII", "SNA", "SO", "LUV", "SOV", "SE", "S", "STJ", "SWK", "SPLS", "SBUX", "HOT", "STT", "SYK", "JAVAD", "SUN", "STI", "SVU", "SYMC", "SNV", "SYY", "TROW", "TGT", "TE", "TLAB", "TIN", "THC", "TDC", "TER", "TEX", "TSO", "TXN", "TXT", "HSY", "TRV", "TMO", "TIF", "TWX", "TIE", "TJX", "TMK", "RIG", "TRB", "TEL", "TYC", "TSN", "USB", "UNP", "UIS", "UNH", "UPS", "X", "UTX", "UNM", "UST", "VFC", "VLO", "VAR", "VRSN", "VZ", "VIA.B", "VNO", "VMC", "WB", "WMT", "WAG", "DIS", "WM", "WMI", "WAT", "WPI", "WFT", "WLP", "WFC", "WEN", "WU", "WY", "WHR", "WFMI", "WMB", "WIN", "WWY", "WYE", "WYN", "XEL", "XRX", "XLNX", "XL", "XTO", "YHOO", "YUM", "ZMH", "ZION"};
            //String[] symbolName = {"AAPL"};
            for ( String symbol : symbolName )
            {
                Contract contract = getContract( symbol );
                wrapper.setSymbol( contract );
                wrapper.startTransaction();
                //wrapper.getSocket().reqHistoricalData( 0, contract, sdf.format( new Date() ), "5 D", "1 min", "TRADES", 1, 1 );
                wrapper.reqHistoricalData( new Date( "1-JAN-2009" ), new Date() );
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

        contract.m_symbol = symbolName;
        contract.m_secType = "STK";
        contract.m_expiry = "";
        contract.m_right = "";
        contract.m_strike = 0;
        contract.m_multiplier = "";
        contract.m_exchange = "SMART";
        contract.m_primaryExch = "NASDAQ";
        contract.m_currency = "USD";
        contract.m_localSymbol = "";

        return contract;
    }
}
