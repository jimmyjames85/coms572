package ai;

import java.io.*;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.Utilities.*;
/**
 * Created by jtappe on 9/23/2015.
 */
public class WebSearch
{
    public static void main(String args[])
    {
        URL location = fileToUrl(new File("C:\\cygwin64\\home\\jtappe\\git\\coms572\\intranets\\intranet1\\page1.html"));
        Page startPage = new Page(location);
        Search search = new Search(startPage, Search.SearchType.BREADTH);
        search.run();
    }

}
