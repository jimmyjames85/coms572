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

    private Link()
    {
    }

    public Link(Page parentPage, String title, String link)
    {
        if(parentPage==null)
            throw new IllegalArgumentException("parentPage cannot be null");
        if(title==null)
            throw new IllegalArgumentException("title cannot be null");
        if(link ==null)
            throw new IllegalArgumentException("link cannot be null");

        this.title = title;
        this.link = link;
        this.parentPage = parentPage;
    }

    public URL toURL()
    {
        try
        {
            return new URL(link); //absolute link
        }
        catch(MalformedURLException e )
        {
            try
            {
                //relative link
                URL parentUrl= parentPage.getUrl();
                return new URL(parentUrl,link);
            }
            catch(MalformedURLException ee )
            {
                println("Bad Link: " + ee);
            }
        }
        return null;
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
