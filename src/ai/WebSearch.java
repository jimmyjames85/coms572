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
        //String[] arr = { "https://www.cyhire.iastate.edu/students/index.php?mode=form&id=7f5b12eb95d56a3b5009568e9721ed97&s=event&ss=cf&__paging=0" , "BREADTH" , "true",  "SALARY LEVEL"};
        //String[] arr = {"https://www.cyhire.iastate.edu/students/index.php?s=employers&ss=employers&mode=profile&id=7c6183382fe3dc02a701eb488eaa6586&cf=cb3660340814e327cef4a877fcae6885&cfc=7f5b12eb95d56a3b5009568e9721ed97&sss=", "BREADTH" , "true",  "getTabContent"};
		String[] arr = {"https://cyhire.iastate.edu/students/index.php?mode=form&id=7f5b12eb95d56a3b5009568e9721ed97&s=event&ss=cf&__paging=0", "BREADTH" , "true",  "getTabContent('postings',"};
        args = arr;
		if (args.length < 2)
		{
			System.out.println("You must provide the directoryName and searchStrategyName.  Please try again.");
		} else
		{
			String directoryName = args[0]; // Read the search strategy to use.
			String searchStrategyName = args[1]; // Read the search strategy to use.
			WebSearch.DEBUG = false;
			String searchString = " QUERY1 QUERY2 QUERY3 QUERY4 ";

			if(args.length>=3)
				WebSearch.DEBUG = Boolean.parseBoolean(args[2]);

			if(args.length>=4)
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
