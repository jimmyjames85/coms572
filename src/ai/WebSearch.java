package ai;


import java.io.*;
import java.net.URL;

import static util.Utilities.*;

/**
 * Created by jtappe on 9/23/2015.
 */
public class WebSearch
{
	public static Boolean DEBUG = false;


	public static void main(String args[]) throws Exception
	{
		if (args.length < 2)
		{
			System.out.println("You must provide the directoryName and searchStrategyName.  Please try again.");
		} else
		{
			String directoryName = args[0]; // Read the search strategy to use.
			String searchStrategyName = args[1]; // Read the search strategy to use.
			WebSearch.DEBUG = false;
			String searchString = " QUERY1 QUERY2 QUERY3 QUERY4 ";

			if(args.length>3)
				WebSearch.DEBUG = Boolean.parseBoolean(args[2]);

			if(args.length>4)
				searchString = args[3];

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
