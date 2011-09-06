package com.avi.core;

import com.avi.util.DownloaderUtilC;

import java.util.Date;

public class EndOfDay
{
    private int id;
    private int symbol;
    private float open;
    private float close;
    private float high;
    private float low;
    private float volume;
    private int tickDate;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
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

    public int getTickDate()
    {
        return tickDate;
    }

    public void setTickDate( int tickDate )
    {
        this.tickDate = tickDate;
    }

    public Date getDate()
    {
        return DownloaderUtilC.getFibboDateFromLong( this.tickDate );
    }

    public void setDate( Date date )
    {
        this.tickDate = DownloaderUtilC.getFibboDateFromDate( date );
    }

}
