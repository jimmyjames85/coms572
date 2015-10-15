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
import java.util.Date;
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
		System.out.println("KColor color arguments");
		System.out.println("");
		System.out.println("       KColor color [STRATEGY] [GRAPH_FILE]");
		System.out.println("");
		System.out.println("              color");
		System.out.println("                          reads in the graph file and attempts to find");
		System.out.println("                          both three and four k-coloring's using the");
		System.out.println("                          strategies outlined in this assignment. If");
		System.out.println("                          [STRATEGY] is not given it will run all");
		System.out.println("                          strategies. ");
		System.out.println("");
		System.out.println("");
		System.out.println("              [STRATEGY] ");
		System.out.println("                          Optional. Must be one of MIN_CONFLICTS,");
		System.out.println("                          BACKTRACKING, FC, AC3, MAC. Specifies the");
		System.out.println("                          backtracking and inference");
		System.out.println("                          preferences. ");
		System.out.println("");
		System.out.println("              [GRAPH_FILE]");
		System.out.println("                          Graph file to color");
		System.out.println("");
		System.out.println("");
		System.out.println("KColor create arguments");
		System.out.println("");
		System.out.println("       KColor create [FROM] [TO] [COPIES] [GRAPH_DIR]");
		System.out.println("");
		System.out.println("              create   ");
		System.out.println("                          will create graph files in GRAPH_DIR that");
		System.out.println("                          can be loaded for the color option. File");
		System.out.println("                          names have the format:");
		System.out.println("");
		System.out.println("                              <node_total>_nodes_<copy_index>.txt");
		System.out.println("");
		System.out.println("                          where each file has node_total nodes in it's");
		System.out.println("                          graph file and copy_index starts at 0 and");
		System.out.println("                          ends at [COPIES]-1. ");
		System.out.println("");
		System.out.println("");
		System.out.println("              [FROM]");
		System.out.println("                          node_total start index (inclusive, must be");
		System.out.println("                          greater than 2)");
		System.out.println("");
		System.out.println("              [TO]");
		System.out.println("                          node_total end index (inclusive)");
		System.out.println("");
		System.out.println("              [COPIES]");
		System.out.println("                          number of copies to create. ");
		System.out.println("");
		System.out.println("              [GRAPH_DIR]");
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

	public static void createGraphs(int from, int to, int copies, File graph_location, MessageLogger logger)
	{
		validateCommands(from, to, copies, graph_location);
		for (int n = from; n <= to; n++)
		{
			Rectangle mapDimensions = new Rectangle(0, 0, 10 * n, 10 * n);
			for (int a = 0; a < copies; a++) //averages
			{
				File f = new File(graph_location, +n + "_nodes_" + a + ".txt");
				logger.log("Creating: " + f);
				UnitSquareMap usm = new UnitSquareMap(n, true, mapDimensions);
				usm.storeToFile(f);
			}
		}
	}

	public static void old_runKColor(int from, int to, int copies, File graph_location, MessageLogger logger)
	{
		validateCommands(from, to, copies, graph_location);

		KColor main = new KColor(logger);

		SearchStrategy strats[] = {SearchStrategy.MIN_CONFLICTS, SearchStrategy.BACKTRACKING, SearchStrategy.FC, SearchStrategy.AC3, SearchStrategy.MAC};

		for (int n = from; n <= to; n++)
		{
			logger.log("========= <nodeTotal, k-color, avg completion rate %, Strategy, avg time > ========= "+graph_location+" =========");

			double completion[] = new double[strats.length * 2];
			long time[] = new long[strats.length * 2];
			for (int i = 0; i < time.length; i++)
			{
				completion[i] = 0;
				time[i] = 0;
			}

			for (int a = 0; a < copies; a++) //averages.........created 10 versions of each n-sized map
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
				time[i] /= copies;
				completion[i] /= copies;

				boolean threeColor = (i % 2 == 0);
				Result r = new Result(strats[i / 2], n, threeColor);
				r.setFinishTime(time[i]);
				r.setAssignmentCompletion(completion[i]);
				logger.log(r.toString() + " ");
				//if (!threeColor)

			}
			logger.log("");
		}
	}

	public static void runKColor(File graph_location, MessageLogger logger)
	{

		File f = graph_location;
		if (!f.exists())
			throw new IllegalArgumentException("File does not exist: " + f.getAbsolutePath());

		KColor main = new KColor(logger);
		SearchStrategy strats[] = {SearchStrategy.MIN_CONFLICTS, SearchStrategy.BACKTRACKING, SearchStrategy.FC, SearchStrategy.AC3, SearchStrategy.MAC};
		logger.log("========= <nodeTotal, k-color, completion %, strategy, time > ========= "+graph_location+" =========");


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
				Result r = main.getResults();
				r.setFinishTime(fin - start);
				logger.log(r.toString());
			}
		}
		logger.log("");
	}

	public static void runKColor(File graph_location, SearchStrategy searchStrategy, MessageLogger logger)
	{
		File f = graph_location;
		if (!f.exists())
			throw new IllegalArgumentException("File does not exist: " + f.getAbsolutePath());

		KColor main = new KColor(logger);
		logger.log("========= <nodeTotal, k-color, completion %, strategy, time > ========= "+graph_location+" =========");

		for (int threeColor = 0; threeColor <= 1; threeColor++) // run it for both 3-color and 4-color
		{
			UnitSquareMap usm = new UnitSquareMap(f, threeColor == 0);
			main.setup(usm, searchStrategy);
			long start = System.currentTimeMillis();
			main.run();
			long fin = System.currentTimeMillis();
			Result r = main.getResults();
			r.setFinishTime(fin - start);
			logger.log(r.toString());
		}
		logger.log("");

	}


	public static void main(String args[])
	{
		String cmds[] = args;
		//String cmds[] = {"color", "100", "100", "10", "graphs"};
		//String cmds[] = {"color", "AC3", "graphs/203_nodes_4.txt"};
		MessageLogger logger = new MessageLogger()
		{
			@Override
			public void log(String message)
			{
				System.out.print("\n[" + new Date(System.currentTimeMillis()).toString() +"]\t"+ message);
			}
		};

		if (cmds.length < 5)
		{

			if (cmds.length < 2 || !cmds[0].equalsIgnoreCase("color"))
			{
				printHelpAndDie();
				System.exit(-1);
			}

			if(cmds.length==2)
			{

				File colorFile = new File(cmds[1]);
				runKColor(colorFile, logger);
			}
			else if(cmds.length==3)
			{
				File colorFile = new File(cmds[2]);
				SearchStrategy searchStrategy = SearchStrategy.valueOf(cmds[1].toUpperCase());
				runKColor(colorFile,searchStrategy,logger);
			}
			System.exit(0);
		}

		if (!cmds[0].equalsIgnoreCase("create"))
			printHelpAndDie();

		int from = 0;
		int to = 0;
		int copies = 0;
		File graph_location = null;

		try
		{
			from = Integer.parseInt(cmds[1]);
			to = Integer.parseInt(cmds[2]);
			copies = Integer.parseInt(cmds[3]);
			graph_location = new File(cmds[4]);
			createGraphs(from, to, copies, graph_location, logger);
		}
		catch (Exception e)
		{
			System.out.println("Error Occured: " + e.getMessage());
			System.exit(-1);
		}
		System.out.println();

	}

}
