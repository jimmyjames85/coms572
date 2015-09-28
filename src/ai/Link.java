package ai;

import static util.Utilities.*;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jtappe on 9/23/2015.
 */
public class Link
{
	private Page parentPage;
	private String title;
	private String link;
	private URL url;

	private int heuristicScore = 0;

	//1 means top ... 2 next link down ...etc
	//0 means unkown
	private int pageLocation = -1;


	private Link()
	{
	}



	public Link(Page parentPage, String title, String link, int pageLocation)
	{
		setupLink(parentPage, title, link, pageLocation);
	}

	public Link(Page parentPage, String title, String link)
	{
		setupLink(parentPage, title, link, -1);
	}

	private void setupLink(Page parentPage, String title, String link, int pageLocation)
	{

/*		if (parentPage == null)
			throw new IllegalArgumentException("parentPage cannot be null");
		if (title == null)
			throw new IllegalArgumentException("title cannot be null");//title = "";
		if (link == null)
			throw new IllegalArgumentException("link cannot be null");//link = "";
*/
		this.title = title;
		this.link = link;
		this.parentPage = parentPage;
		this.url = createUrlFromLink();
		setPageLocation(pageLocation);

	}

	private URL createUrlFromLink()
	{
		try
		{
			if (link != null)
				return new URL(link); //absolute link
		}
		catch (MalformedURLException e)
		{
			try
			{
				//relative link
				if (parentPage != null)
				{
					URL parentUrl = parentPage.getUrl();
					return new URL(parentUrl, link);
				}
			}
			catch (MalformedURLException ee)
			{
				println("Bad Link: " + ee);
			}
		}
		return null;
	}

	public URL toURL()
	{
		return this.url;
	}

	public Page getParentPage()
	{
		return parentPage;
	}

	public String getTitle()
	{
		return title;
	}

	public String getLink()
	{
		return link;
	}

	public int getPageLocation()
	{
		return pageLocation;
	}

	public void setPageLocation(int location)
	{
		if (pageLocation <= 0)
			pageLocation = 0;
		this.pageLocation = location;
	}

	public int getHeuristicScore()
	{
		return heuristicScore;
	}

	public void setHeuristicScore(int heuristicScore)
	{
		this.heuristicScore = heuristicScore;
	}

	@Override
	public String toString()
	{
		return "Link{" +
				"parentPage=" + parentPage +
				", title='" + title + '\'' +
				", link='" + link + '\'' +
				'}';
	}
}
