package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jim on 9/24/15.
 */
public class HeuristicFunctionImpl implements HeuristicFunction<Link, String>
{
	private String goalStr;
	private String any_single_keyword_regex;
	private String any_consecutive_keywords_regex;
	private List<String> any_consecutive_in_order_keywords_regexList = new ArrayList<String>();

	public void setGoal(String goalString)
	{
		if(goalString==null || goalString.trim().equals(""))
		{
			goalStr = null;
			any_single_keyword_regex=null;
			any_consecutive_keywords_regex=null;
			any_consecutive_in_order_keywords_regexList = new ArrayList<String>();
			return;
		}
		this.goalStr = goalString;

		String[] key_words = goalStr.split("\\s");
		any_single_keyword_regex = "";

		if (key_words.length > 0)
			any_single_keyword_regex += "(" + key_words[0] + "\\s*)";

		for (int i = 1; i < key_words.length; i++)
			any_single_keyword_regex += "|(" + key_words[i] + "\\s*)";

		any_consecutive_keywords_regex = "(((" + any_single_keyword_regex + "))(" + any_single_keyword_regex + ")+)";

		any_consecutive_in_order_keywords_regexList = new ArrayList<String>();

		// We start by picking in-order pairs of keywords for a regex
		// There will be <key_words.length-1> in-order pairs to pick from
		// Then we pick in-order triplets of keywords
		// There will be <key_words.length-2> in-order triplets to pick from
		//....etc

		// WE pick n-tuples
		for (int n = 2; n <= key_words.length; n++)
		{
			//Iterate throught the actual t-tuple
			int tuple_total = key_words.length - n;
			for (int t = 0; t <= tuple_total; t++)
			{
				String regex = "(" + key_words[t] + "\\s*)";

				for (int i = 1; i < n; i++)
					regex += "(" + key_words[t + i] + "\\s*)";
				any_consecutive_in_order_keywords_regexList.add(regex);
			}
		}
	}

	public HeuristicFunctionImpl(String goalStr)
	{
		setGoal(goalStr);
	}

	@Override
	public int calculateCost(Link link)
	{
		if(goalStr==null )
			throw new IllegalStateException("Must define goal String.");

		Page parentPage = link.getParentPage();

		if(parentPage==null )
			throw new IllegalArgumentException("Parent page must not be null.");

		String content = parentPage.getContent();
		if(content == null)
			throw new IllegalArgumentException("Parent page content must not be null.");

		Results contentMatches = countMatches(content);
		Results linkMatches = countMatches(link.getTitle());

		int contentScore = contentMatches.singleMatchCount;//+ 4*contentMatches.consecutiveMatchCount + 10*contentMatches.consecutiveInOrderMatchCount;
		int linksScore = linkMatches.singleMatchCount + 4*linkMatches.consecutiveMatchCount + 10*linkMatches.consecutiveInOrderMatchCount;

		int totalLinks = link.getParentPage().extractLinks().size();
		int linkLoc = link.getPageLocation();
		if(linkLoc<0)
			linkLoc = 0;

		return (-5*contentScore - 30*linksScore) + 20*linkLoc;
	}


	private Results countMatches(String content)
	{
		Pattern pattern = Pattern.compile(any_single_keyword_regex);
		Matcher matcher = pattern.matcher(content);

		int singleMatchCount = 0;
		while (matcher.find())
			singleMatchCount++;

		pattern = Pattern.compile(any_consecutive_keywords_regex);
		matcher = pattern.matcher(content);

		ArrayList<String> consecutiveMatches = new ArrayList<String>();
		int consecutiveMatchCount = 0;
		while (matcher.find())
		{
			consecutiveMatches.add(matcher.group(1));
			consecutiveMatchCount++;
		}

		int consecutiveInOrderMatchCount = 0;
		for (String cons : consecutiveMatches)
		{
			for (String regex : any_consecutive_in_order_keywords_regexList)
			{
				pattern = Pattern.compile(regex);
				matcher = pattern.matcher(cons);
				while (matcher.find())
					consecutiveInOrderMatchCount++;
			}

		}

		Results r = new Results();
		r.consecutiveInOrderMatchCount = consecutiveInOrderMatchCount;
		r.singleMatchCount = singleMatchCount;
		r.consecutiveMatchCount = consecutiveMatchCount;
		return r;
	}

	private class Results
	{
		private int singleMatchCount;
		private int consecutiveMatchCount;
		private int consecutiveInOrderMatchCount;
	}
}
