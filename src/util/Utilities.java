package util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jtappe on 9/23/2015.
 */
public class Utilities
{
    private static Boolean DEBUG = true;

    public static synchronized void print(Object o)
    {
        System.out.print(o);
    }

    public static synchronized void println(Object o)
    {
        System.out.println(o);
    }

    public static synchronized void println()
    {
        System.out.println();
    }

    public static synchronized void debug(Object o)
    {
        if (DEBUG)
            System.out.println(o);
    }

    public static synchronized void debug()
    {
        if (DEBUG)
            System.out.println();
    }

    public static synchronized String getURLContents(URL url)
    {
        String ret = null;
        InputStream is=null;
        try
        {
            is = (InputStream) url.getContent();
            ret = inputStreamToString(is);
        } catch (IOException e)
        {
            println("getURLContents: " + e);
        }
        finally
        {
            try
            {
                if(is!=null)
                    is.close();
            }
            catch (IOException e)
            {
                println("getURLContents: " + e);
            }
        }
        return ret;
    }

    public static synchronized String inputStreamToString(InputStream inputStream) throws IOException
    {
        StringBuffer str = new StringBuffer("");
        while (inputStream.available() > 0)
            str.append((char)inputStream.read());

        return str.toString();
    }

    public static synchronized URL fileToUrl(File file)
    {
        URL ret = null;
        try
        {
            ret = file.toURI().toURL();
        } catch (MalformedURLException e)
        {
            println("Exception in fileToUrl(" + file.getAbsolutePath() + "), msg=" + e);
        }
        return ret;
    }

    public static synchronized URL createURL(String urlString)
    {
        URL ret = null;
        try
        {
            ret = new URL(urlString);
        } catch (MalformedURLException e)
        {
            println("Exception in createURL(" + urlString + "), msg=" + e);
        }
        return ret;
    }
}
