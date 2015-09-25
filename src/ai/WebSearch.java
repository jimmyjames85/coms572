package ai;


import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.Utilities.*;

/**
 * Created by jtappe on 9/23/2015.
 */
public class WebSearch
{

	public static void testSearches()
	{
		Search.SearchType searchType = Search.SearchType.BEAM;

		URL intranet_location = fileToUrl(new File("/home/jim/git/coms572/intranets/intranet7/page1.html"));
		String intranet_searchString = " QUERY1 QUERY2 QUERY3 QUERY4 ";
		Page intranet_startPage = new Page(intranet_location);


		URL internet_location = createURL("http://web.cs.iastate.edu/~jmtappe/arduino/documentation/blinkInC.html");

		internet_location = createURL("https://en.wikipedia.org/wiki/Arnold_Schwarzenegger");

		String internet_searchString = "This simple table shows a quick comparison between the characteristics of all the Arduino boards.";
		internet_searchString = "Kevin Bacon";
		Page internet_startPage = new Page(internet_location);

		Search intranet_search = new Search(intranet_startPage, searchType, intranet_searchString);

		intranet_search.setHeuristicFunction(new HeuristicFunctionImpl(intranet_searchString));

		Search internet_search = new Search(internet_startPage, searchType, internet_searchString);
		internet_search.setBeamFrontierSize(50);
		internet_search.setHeuristicFunction(new HeuristicFunctionImpl(internet_searchString));
		intranet_search.run();
		//internet_search.run();
	}

	public static void main(String args[]) throws Exception
	{
		if (args.length < 2)
		{
			System.out.println("You must provide the directoryName and searchStrategyName.  Please try again.");
		} else
		{
			String directoryName = args[0]; // Read the search strategy to use.
			String searchStrategyName = args[1]; // Read the search strategy to use.
			String searchString = " QUERY1 QUERY2 QUERY3 QUERY4 ";
			if(args.length>2)
				searchString = args[2];

			if (searchStrategyName.equalsIgnoreCase("breadth") ||
					searchStrategyName.equalsIgnoreCase("depth") ||
					searchStrategyName.equalsIgnoreCase("best") ||
					searchStrategyName.equalsIgnoreCase("beam"))
			{

				URL url;
				File dirName = new File(directoryName);
				if(dirName.exists())
					url = fileToUrl(dirName);
				else
					url = createURL(directoryName);


				Search search = new Search(new Page(url), Search.SearchType.valueOf(searchStrategyName.toUpperCase()),searchString);
				search.setHeuristicFunction(new HeuristicFunctionImpl(searchString));
				search.setBeamFrontierSize(10);
				search.run();

			} else
			{
				System.out.println("The valid search strategies are:");
				System.out.println("  BREADTH DEPTH BEST BEAM");
			}
		}
	}

}
