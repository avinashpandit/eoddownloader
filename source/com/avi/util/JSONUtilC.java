package com.avi.util;

import com.avi.core.JSONInterface;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: pandit
 * Date: Jul 14, 2008
 * Time: 5:54:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSONUtilC
{

    public static String getJSONMessage( Collection<JSONInterface> col )
    {
        StringBuilder str = new StringBuilder( 100 ).append( "{\"page\":\"1\",\"total\":\"1\",\"records\":\"" ).append( col.size() ).append( "\",\"rows\":[" );
        for ( JSONInterface jsonObj : col )
        {
            str.append( jsonObj.getJSONData() ).append( ',' );
        }
        if ( !col.isEmpty() )
        {
            str.deleteCharAt( str.length() - 1 );
        }
        str.append( "]}" );
        return str.toString();
    }

}
