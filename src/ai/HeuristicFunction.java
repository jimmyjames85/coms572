package ai;

/**
 * Created by jim on 9/23/15.
 */
public interface HeuristicFunction<N, G>
{
	// positve numbers are costly
	// negative number are favorable
	public int calculateCost(N node);


	public void setGoal(G goalString);

}
