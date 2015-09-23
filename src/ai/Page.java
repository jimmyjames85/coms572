package ai;

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
    private String data;

    private Page()
    {//No Default Constructor
    }

    public Page(URL url)
    {
        if (url == null)
            throw new IllegalArgumentException("Page: URL cannot be null");

        this.url = url;
        data = getURLContents(url);
        if (data == null)
            throw new IllegalArgumentException("Page: Unable to retrieve data from URL: " + url);

    }

    public URL getUrl()
    {
        return this.url;
    }

    public List<Link> extractLinks()
    {
        if (data == null)
            return null;

        List<Link> ret = new ArrayList<Link>();

        Pattern pattern = Pattern.compile("<\\s*[aA]\\s*[hH][rR][eE][fF]\\s*=\\s*([ \"])([^> \"]*)\\1[^>]*>(([^<]*))</[aA]\\s*>");
        Matcher matcher = pattern.matcher(data);
        while (matcher.find())
        {
            if (matcher.groupCount() >= 3)
            {
                String loc = matcher.group(2); //TODO sanitize???
                String title = matcher.group(3);

                Link lnk = new Link(this, title, loc);
                ret.add(lnk);
            }
        }
        return ret;
    }

    public String getData()
    {
        return data;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
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
