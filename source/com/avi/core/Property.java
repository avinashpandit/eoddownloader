package com.avi.core;

/**
 * Created by IntelliJ IDEA.
 * User: avinash
 * Date: Aug 2, 2009
 * Time: 6:58:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class Property
{
    private int id;
    private String key;
    private String value;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
}
