package com.avi.downloader;

/**
 * Created by IntelliJ IDEA.
 * User: avinash
 * Date: Jun 8, 2008
 * Time: 11:01:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class Instrument
{
    private Long id;
    private String name;

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }
}
