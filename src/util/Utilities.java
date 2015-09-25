package util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtappe on 9/23/2015.
 */
public class Utilities
{
	private static Boolean DEBUG = false;

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

	public static synchronized void printf(String fmt, Object... args)
	{
		System.out.printf(fmt, args);
	}


	public static synchronized void error(Object o)
	{
		System.err.println(o);
	}

	public static synchronized void debugf(String fmt, Object... args)
	{
		if(DEBUG)
			System.out.printf(fmt, args);
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

	public static synchronized String getURLContent(URL url)
	{
		String ret = null;
		InputStream is = null;
		try
		{
			is = (InputStream) url.getContent();
			ret = inputStreamToString(is);
		}
		catch (IOException e)
		{
			println("getURLContent: " + e);
		}
		finally
		{
			try
			{
				if (is != null)
					is.close();
			}
			catch (IOException e)
			{
				println("getURLContent: " + e);
			}
		}
		return ret;
	}

	public static synchronized String inputStreamToString(InputStream inputStream) throws IOException
	{
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try
		{

			br = new BufferedReader(new InputStreamReader(inputStream));
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

	public static synchronized URL fileToUrl(File file)
	{
		URL ret = null;
		try
		{
			ret = file.toURI().toURL();
		}
		catch (MalformedURLException e)
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
		}
		catch (MalformedURLException e)
		{
			println("Exception in createURL(" + urlString + "), msg=" + e);
		}
		return ret;
	}

	public static <E> List<E> reverseList(List<E> list)
	{
		List<E> ret = new ArrayList<E>();
		int size = list.size();
		for(int i=size-1;i>=0;i--)
			ret.add(list.get(i));

		return ret;
	}

	public static String tabs(int count)
	{
		StringBuffer sb = new StringBuffer("");
		for(int i=0;i<count;i++)
			sb.append("\t");
		return sb.toString();
	}


}
