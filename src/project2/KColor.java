package project2;

import aima.core.agent.Action;
import aima.core.agent.Agent;
import aima.core.agent.EnvironmentState;
import aima.core.agent.EnvironmentView;
import aima.core.search.csp.*;
import aima.core.util.datastructure.FIFOQueue;
import aima.gui.applications.search.csp.CSPEnvironment;
import aima.gui.framework.MessageLogger;
import org.htmlparser.util.ParserException;


import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Created by jim on 10/10/15.
 */
public class KColor implements EnvironmentView
{
	private CSPEnvironment env;
	private FIFOQueue<CSPEnvironment.StateChangeAction> actions;
	private int actionCount;
	private UnitSquareMap usm;
	private MessageLogger logger;
	private boolean verbose;
	private SearchStrategy searchStrategy;

	public KColor(MessageLogger logger)
	{
		this.logger = logger;
		verbose = false;
	}

	public boolean isVerbose()
	{
		return verbose;
	}

	public void setVerbosity(boolean verbose)
	{
		this.verbose = verbose;
	}

	@Override
	public void notify(String msg)
	{
		if (verbose)
			logger.log(msg);
	}

	@Override
	public void agentAdded(Agent agent, EnvironmentState resultingState)
	{
	}

	@Override
	public void agentActed(Agent agent, Action action, EnvironmentState resultingState)
	{
		if (verbose)
			notify(actions.toString());
	}

	public void setup(UnitSquareMap usm, SearchStrategy searchStrategy)
	{

		this.usm = usm;
		this.searchStrategy = searchStrategy;
		env = new CSPEnvironment();
		env.addEnvironmentView(this); //For logging purposes
		actions = new FIFOQueue<CSPEnvironment.StateChangeAction>();
		//usm.updateCSPView(view); //gui
		actions.clear();
		actionCount = 0;
		//view.setEnvironment(env); //gui
		//env.init(csp);
		env.init(usm);
		//view.setEnvironment(env); //gui
	}


	protected void prepareActions()
	{
		ImprovedBacktrackingStrategy iStrategy = null;
		if (actions.isEmpty())
		{
			SolutionStrategy strategy = null;
			switch (searchStrategy)
			{
				case BACKTRACKING:
					strategy = new BacktrackingStrategy();
					break;
				case MRV_DEG: // MRV + DEG
					strategy = new ImprovedBacktrackingStrategy(true, true, false, false);
					break;
				case FC: // FC
					iStrategy = new ImprovedBacktrackingStrategy();
					iStrategy.setInference(ImprovedBacktrackingStrategy.Inference.FORWARD_CHECKING);

					break;
				case MRV_FC: // MRV + FC
					iStrategy = new ImprovedBacktrackingStrategy(true, false, false, false);
					iStrategy.setInference(ImprovedBacktrackingStrategy.Inference.FORWARD_CHECKING);
					break;
				case FC_LCV: // FC + LCV
					iStrategy = new ImprovedBacktrackingStrategy(false, false, false, true);
					iStrategy.setInference(ImprovedBacktrackingStrategy.Inference.FORWARD_CHECKING);
					break;
				case AC3: // AC3
					strategy = new ImprovedBacktrackingStrategy(false, false, true, false);
					break;
				case MRV_DEG_AC3_LCV: // MRV + DEG + AC3 + LCV
					strategy = new ImprovedBacktrackingStrategy(true, true, true, true);
					break;
				case MIN_CONFLICTS:
					strategy = new MinConflictsStrategy(50);
					break;
				case MAC:
					iStrategy = new ModifiedImprovedBacktrackingStrategy();
					((ModifiedImprovedBacktrackingStrategy) iStrategy).setInference(ModifiedImprovedBacktrackingStrategy.Inference.MAC);
					break;
			}
			if (iStrategy != null)
				strategy = iStrategy;

			try
			{
				strategy.addCSPStateListener(new CSPStateListener()
				{
					@Override
					public void stateChanged(Assignment assignment, CSP csp)
					{
						actions.add(new CSPEnvironment.StateChangeAction(assignment, csp));
					}

					@Override
					public void stateChanged(CSP csp)
					{
						actions.add(new CSPEnvironment.StateChangeAction(csp));
					}
				});
				strategy.solve(env.getCSP().copyDomains());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public Result getResults()
	{

		Result ret = new Result(searchStrategy, usm.getNodeTotal(), usm.getThreeColors());

		ret.setFinalAssignment("");
		ret.setFinishTime(-1);

		int assignmentCount = 0;

		StringBuilder sb = new StringBuilder(" ");
		if (env == null)
		{
			ret.setFinalAssignment("[No assignments yet]");
			return ret;
		}
		List<Variable> vars = env.getCSP().getVariables();
		for (int i = 0; i < vars.size(); i++)
		{
			Variable var = vars.get(i);
			sb.append(var.toString() + ":=");
			Assignment assignment = env.getAssignment();

			Object value = null;
			if (assignment != null)
				value = assignment.getAssignment(var);
			if (value != null)
			{
				sb.append(value);
				assignmentCount++;
			} else
				sb.append("(???)");


			if (i < vars.size() - 1)
				sb.append(", ");
		}
		sb.append(" ]");
		double percentage = (double) assignmentCount / (double) vars.size();
		percentage *= 100;
		ret.setAssignmentCompletion(percentage);
		ret.setFinalAssignment("[ " + sb.toString());

		return ret;

	}

	public void run()
	{
		if (verbose)
			logger.log("<simulation-log>");

		prepareActions();

		while (!actions.isEmpty())// && !frame.simulationPaused())
		{
			env.executeAction(null, actions.pop());
			actionCount++;
		}
		if (verbose)
		{
			logger.log("Number of Steps: " + actionCount);
			// logger.log(getStatistics());
			logger.log("</simulation-log>\n");
		}
	}

	private static class Result
	{
		SearchStrategy strat;
		int nodeTotal;
		boolean threeColors;
		long millis = 0;
		String finalAssignment;
		double assignmentCompletion;

		public Result(SearchStrategy strategy, int nodeTotal, boolean threeColors)
		{
			this.strat = strategy;
			this.nodeTotal = nodeTotal;
			this.threeColors = threeColors;
		}

		public void setAssignmentCompletion(double assignmentCompletion)
		{
			this.assignmentCompletion = assignmentCompletion;
		}

		public void setFinishTime(long millis)
		{
			this.millis = millis;
		}

		public void setFinalAssignment(String finalAssignment)
		{
			this.finalAssignment = finalAssignment;
		}


		public String toString()
		{
			int c = 3;
			if (!threeColors)
				c = 4;
			String percentage = String.format("%3.2f", assignmentCompletion) + "%";
			return "<" + nodeTotal + ", " + c + ", " + percentage + ", " + strat.toString() + ", " + millis + " ms>";//, " + finalAssignment + ">";
		}
	}


	public static void printHelpAndDie()
	{
		System.out.println("KColor arguments");
		System.out.println("");
		System.out.println("       KColor [OPTION] [FROM] [TO] [ITR_COUNT] [GRAPH_DIR] ");
		System.out.println("");
		System.out.println("              [OPTION] = create | color");
		System.out.println("");
		System.out.println("                          create - will create graph files in");
		System.out.println("                                   GRAPH_DIR that can be loaded for");
		System.out.println("                                   the color option File names have");
		System.out.println("                                   the format:");
		System.out.println("                                   ");
		System.out.println("                                     <node_total>_nodes_<itr_count>.txt");
		System.out.println("");
		System.out.println("                                   Where each file has node_total");
		System.out.println("                                   nodes in it's graph file and");
		System.out.println("                                   itr_count starts at 0 and ends at");
		System.out.println("                                   AVG_COUNT-1.");
		System.out.println("");
		System.out.println("                          color - reads in graphs from the GRAPHS_DIR");
		System.out.println("                                  and attempts to find k-coloring's");
		System.out.println("                                  using the strategies outlined in");
		System.out.println("                                  this assignment.  Sends output to");
		System.out.println("                                  stdout. File names must be that of");
		System.out.println("                                  outputed format using the create");
		System.out.println("                                  command.");
		System.out.println("");
		System.out.println("              [FROM]  ");
		System.out.println("                          node_total start index (inclusive, must be");
		System.out.println("                          greater than 2)");
		System.out.println("");
		System.out.println("              [TO]    ");
		System.out.println("                          node_total end index (inclusive)");
		System.out.println("");
		System.out.println("              [ITR_COUNT] ");
		System.out.println("                          Number of iterations (itr_count) to");
		System.out.println("                          create. When coloring, the algorith runs a");
		System.out.println("                          strategy ITR_COUNT times and takes the");
		System.out.println("                          average runtime.");
		System.out.println("");
		System.out.println("              [GRAPH_DIR] ");
		System.out.println("                          where to save or load graphs from");
		System.exit(-1);
	}

	private static void validateCommands(int from, int to, int averageCount, File graph_location)
	{
		if (!graph_location.exists())
			throw new IllegalArgumentException(graph_location.getAbsolutePath() + " doesn't exist");
		if (!graph_location.isDirectory())
			throw new IllegalArgumentException(graph_location.getAbsolutePath() + " must be a directory");


		if (from < 3)
			throw new IllegalArgumentException("Starting node count must be greater than two: " + from);

		if (from > to)
			throw new IllegalArgumentException("Starting node count must be less than or equal to ending node count: " + from + " > " + to);

		if (averageCount < 1)
			throw new IllegalArgumentException("Average request count must be greater than or equal to one: " + averageCount);

	}

	public static void createGraphs(int from, int to, int averageCount, File graph_location)
	{

		validateCommands(from, to, averageCount, graph_location);
		for (int n = from; n <= to; n++)
		{
			Rectangle mapDimensions = new Rectangle(0, 0, 10 * n, 10 * n);
			for (int a = 0; a < averageCount; a++) //averages
			{
				File f = new File(graph_location, +n + "_nodes_" + a + ".txt");
				System.out.println("Creating: " + f);
				UnitSquareMap usm = new UnitSquareMap(n, true, mapDimensions);
				usm.storeToFile(f);
			}
		}
	}

	public static void runKColor(int from, int to, int averageCount, File graph_location)
	{
		validateCommands(from, to, averageCount, graph_location);


		MessageLogger logger = new MessageLogger()
		{
			@Override
			public void log(String message)
			{
				System.out.print(message);
			}
		};
		KColor main = new KColor(logger);

		SearchStrategy strats[] = {SearchStrategy.MIN_CONFLICTS, SearchStrategy.BACKTRACKING, SearchStrategy.FC, SearchStrategy.AC3, SearchStrategy.MAC};

		for (int n = from; n <= to; n++)
		{
			logger.log("\n========= <nodeTotal, k-color, avg completion rate %, Strategy, avg time > =========\n");

			double completion[] = new double[strats.length * 2];
			long time[] = new long[strats.length * 2];
			for (int i = 0; i < time.length; i++)
			{
				completion[i]=0;
				time[i] = 0;
			}

			for (int a = 0; a < averageCount; a++) //averages.........created 10 versions of each n-sized map
			{
				File f = new File(graph_location, +n + "_nodes_" + a + ".txt");
				if (!f.exists())
					throw new IllegalArgumentException("File does not exist: " + f.getAbsolutePath());

				for (int s = 0; s < strats.length; s++) //after file is open run every strategy on it
				{
					SearchStrategy searchStrategy = strats[s];

					for (int threeColor = 0; threeColor <= 1; threeColor++) // run it for both 3-color and 4-color
					{
						UnitSquareMap usm = new UnitSquareMap(f, threeColor == 0);
						main.setup(usm, searchStrategy);
						long start = System.currentTimeMillis();
						main.run();
						long fin = System.currentTimeMillis();
						time[2 * s + threeColor] += (fin - start);
						completion[2 * s + threeColor] += main.getResults().assignmentCompletion;
					}
				}
			}

			for (int i = 0; i < time.length; i++)
			{
				time[i] /= averageCount;
				completion[i] /= averageCount;

				boolean threeColor = (i % 2 == 0);
				Result r = new Result(strats[i / 2], n, threeColor);
				r.setFinishTime(time[i]);
				r.setAssignmentCompletion(completion[i]);
				logger.log(r.toString() + " ");
				//if (!threeColor)
					logger.log("\n");
			}
		}
	}

	public static void main(String args[])
	{
		String cmds[] = args;
		//String cmds[] = {"color", "100", "100", "10", "graphs"};

		if (cmds.length < 5)
		{
			printHelpAndDie();
			System.exit(-1);
		}


		boolean create = true;
		if (cmds[0].equalsIgnoreCase("create"))
		{
			create = true;

		} else if (cmds[0].equalsIgnoreCase("color"))
		{
			create = false;
		} else
		{
			printHelpAndDie();
		}

		int from = 0;
		int to = 0;
		int averageCount = 0;
		File graph_location = null;

		try
		{
			from = Integer.parseInt(cmds[1]);
			to = Integer.parseInt(cmds[2]);
			averageCount = Integer.parseInt(cmds[3]);
			graph_location = new File(cmds[4]);


			if (create)
			{
				createGraphs(from, to, averageCount, graph_location);
			} else
			{
				runKColor(from, to, averageCount, graph_location);
			}

		}
		catch (Exception e)
		{
			System.out.println("Error Occured: " + e.getMessage());
			System.exit(-1);
		}

	}

}
