package ai;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.Utilities.*;

/**
 * Created by jtappe on 9/23/2015.
 */
public class Page
{

	private URL url;
	private String content;

	private Page()
	{//No Default Constructor
	}

	public Page(URL url)
	{
		if (url == null)
			throw new IllegalArgumentException("Page: URL cannot be null");

		this.url = url;
	}

	public URL getUrl()
	{
		return this.url;
	}

	private int extractLinkCallCount = 0;

	private List<Link> extractedLinks;

	public List<Link> extractLinks()
	{

		if(extractedLinks!=null)
			return extractedLinks;

		//debugf("Extracting (called %d times) Links from %s\n", ++extractLinkCallCount, url.toExternalForm());

		String content = getContent();
		if (content == null)
		{
			return new ArrayList<Link>();
		}

		int pageLoc = 1;
		List<Link> ret = new ArrayList<Link>();
		Object filter = new NodeClassFilter(LinkTag.class);

		try
		{
			//org.htmlparser.Parser parser = new Parser(this.url.toExternalForm());
			Parser parser = Parser.createParser(content, "UTF-8");
			NodeList nodeList = parser.extractAllNodesThatMatch((NodeFilter) filter);
			for (int e = 0; e < nodeList.size(); e++)
			{
				org.htmlparser.Node ahref = nodeList.elementAt(e);
				String tagHTML = ahref.toHtml();
				String linkTitle = "";
				String linkUrl = null;
				if (ahref.getFirstChild() != null)
					linkTitle = ahref.getFirstChild().toHtml();

				Pattern pattern = Pattern.compile("<[aA]\\s*[^>]*[hH][rR][eE][fF]\\s*=\\s*([ \"])(([^> \"]*))\\1");
				Matcher matcher = pattern.matcher(tagHTML);
				if (matcher.find() && matcher.groupCount() >= 3)
				{
					linkUrl = matcher.group(2);
					/*
					if(linkUrl!=null && linkUrl.indexOf("http",1)>0)
					{
						linkUrl = linkUrl.substring(linkUrl.indexOf("http",1));
						error("REEEEDDDIIIRREECT: " + linkUrl);
					}
					*/


					//debugf("New Link\n\tTitle: %s\n\tURL: %s\n" , linkTitle,linkUrl);
					Link lnk = new Link(this, linkTitle, linkUrl, pageLoc++);
					ret.add(lnk);
				}
			}
		}
		catch (ParserException pe)
		{
			error("ParserException: " + pe);
		}

		this.extractedLinks = ret;
		return ret;

	}

	public String getContent()
	{
		if (content == null)
			content = getURLContent(url);

		return content;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Page page = (Page) o;

		return url.getPath().equals(page.url.getPath());
	}

	@Override
	public int hashCode()
	{
		return url.hashCode();
	}

	@Override
	public String toString()
	{
		return "Page{" +
				"url=" + url +
				'}';
	}
}
