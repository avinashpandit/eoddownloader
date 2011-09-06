package com.avi.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: avinash
 * Date: Feb 15, 2009
 * Time: 8:40:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntraDay
{
    private static DateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" );
    private static DateFormat todayDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );

    private int id;
    private int symbol;
    private float open;
    private float close;
    private float high;
    private float low;
    private float volume;
    private long tickDate;
    private String tickDateStr;
    private String name;
    private Byte source;
    private String type;


    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public void parseIndiaBulls( String str )
    {
        GregorianCalendar today = new GregorianCalendar();
        today.setTime( new Date() );
        todayDateFormat.setTimeZone( TimeZone.getTimeZone( "IST" ) );
        String todayStr = todayDateFormat.format( today.getTime() );

        StringTokenizer tokenEQ = new StringTokenizer( str, "^" );
        int counter = 0;
        String strToken;
        while ( tokenEQ.hasMoreTokens() )
        {
            strToken = tokenEQ.nextToken();
            switch ( counter )
            {
                case 1:
                    name = strToken;
                    if ( name.endsWith( "EQ" ) )
                    {
                        type = "EQ";
                    }
                    else
                    {
                        type = "IX";
                        counter = 14;
                    }
                    break;
                case 8:
                    volume = Long.parseLong( strToken );
                    break;
                case 10:
                    open = Long.parseLong( strToken ) * 1.0f / 100.0f;
                    break;
                case 11:
                    high = Long.parseLong( strToken ) * 1.0f / 100.0f;
                    break;
                case 12:
                    low = Long.parseLong( strToken ) * 1.0f / 100.0f;
                    break;
                case 13:
                    close = Long.parseLong( strToken ) * 1.0f / 100.0f;
                    break;
                case 19:
                    tickDateStr = strToken;
                    try
                    {
                        tickDate = dateFormat.parse( todayStr + ' ' + tickDateStr ).getTime();
                    }
                    catch ( ParseException e )
                    {

                    }
                    break;
                case 21:
                    name = strToken;
                    break;
            }
            counter++;
        }
    }

    public int getSymbol()
    {
        return symbol;
    }

    public void setSymbol( int symbol )
    {
        this.symbol = symbol;
    }

    public float getOpen()
    {
        return open;
    }

    public void setOpen( float open )
    {
        this.open = open;
    }

    public float getClose()
    {
        return close;
    }

    public void setClose( float close )
    {
        this.close = close;
    }

    public float getHigh()
    {
        return high;
    }

    public void setHigh( float high )
    {
        this.high = high;
    }

    public float getLow()
    {
        return low;
    }

    public void setLow( float low )
    {
        this.low = low;
    }

    public float getVolume()
    {
        return volume;
    }

    public void setVolume( float volume )
    {
        this.volume = volume;
    }

    public long getTickDate()
    {
        return tickDate;
    }

    public void setTickDate( long tickDate )
    {
        this.tickDate = tickDate;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Byte getSource()
    {
        return source;
    }

    public void setSource( Byte source )
    {
        this.source = source;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return new StringBuilder( 30 ).append( name ).append( ',' ).append( open ).append( ',' ).append( close ).append( ',' ).append( high ).append( ',' ).append( low ).append( ',' ).append( volume ).append( ',' ).append( tickDate ).toString();
    }


}
