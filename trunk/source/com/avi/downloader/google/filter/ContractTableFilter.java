package com.avi.downloader.google.filter;


import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;

public class ContractTableFilter implements NodeFilter
{

    protected String mName = "TABLE";

    /**
     * Get the tag name.
     *
     * @return Returns the name of acceptable tags.
     */
    public String getName()
    {
        return ( mName );
    }


    /**
     * Accept nodes that are tags and have a matching tag name.
     * This discards non-tag nodes and end tags.
     * The end tags are available on the enclosing non-end tag.
     *
     * @param node The node to check.
     * @return <code>true</code> if the tag name matches,
     *         <code>false</code> otherwise.
     */
    public boolean accept( Node node )
    {
        try
        {
            if ( ( node instanceof Tag )
                    && !( ( Tag ) node ).isEndTag()
                    && ( ( Tag ) node ).getTagName().equals( mName )
                    && ( ( Tag ) node ).getAttribute( "id" ).equals( "historical_price" )
                    && ( ( Tag ) node ).getAttribute( "class" ).equals( "gf-table" ) )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch ( NullPointerException e )
        {
            return false;
        }
    }
}
