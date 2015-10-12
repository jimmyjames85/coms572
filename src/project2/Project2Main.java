package project2;

import aima.core.agent.Action;
import aima.core.agent.Agent;
import aima.core.agent.EnvironmentState;
import aima.core.agent.EnvironmentView;
import aima.core.search.csp.*;
import aima.core.util.datastructure.FIFOQueue;
import aima.gui.applications.search.csp.CSPEnvironment;
import aima.gui.framework.MessageLogger;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jim on 10/10/15.
 */
public class Project2Main implements EnvironmentView
{
	private CSPEnvironment env;
	private FIFOQueue<CSPEnvironment.StateChangeAction> actions;
	private int actionCount;
	private UnitSquareMap usm;
	private MessageLogger logger;
	private boolean verbose;
	private SearchStrategy searchStrategy;

	public Project2Main(MessageLogger logger)
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
		Result ret = new Result(searchStrategy,usm.getNodeTotal(),usm.getThreeColors());

		ret.setFinalAssignment("");
		ret.setFinishTime(-1);

		int assignmentCount=0;

		StringBuilder sb = new StringBuilder(" ");
		if (env == null)
		{
			ret.setFinalAssignment( "[No assignments yet]");
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
			}
			else
				sb.append("(???)");


			if (i < vars.size() - 1)
				sb.append(", ");
		}
		sb.append(" ]");
		double percentage = (double)assignmentCount/(double)vars.size();
		percentage*=100;
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
			return "<" + nodeTotal + ", " + c+ ", " + percentage  + ", " + strat.toString() + ", " + millis + " ms>";//, " + finalAssignment + ">";
		}
	}

	public static void main(String args[])
	{
		MessageLogger logger = new MessageLogger()
		{
			@Override
			public void log(String message)
			{
				System.out.print(message);
			}
		};
		Project2Main main = new Project2Main(logger);

		SearchStrategy strats[] = {SearchStrategy.MIN_CONFLICTS, SearchStrategy.BACKTRACKING, SearchStrategy.FC, SearchStrategy.AC3, SearchStrategy.MAC};

		int averageCount=10;
		for (int n = 3; n <= 1000; n++)
		{
			Rectangle mapDimensions = new Rectangle(0, 0, 10 * n, 10 * n);

			for (SearchStrategy s : strats)
			{
				for (int i = 0; i < 2; i++)//3color or 4color
				{
					boolean threeColors = i % 2 == 1;

					long sum = 0;
					for(int a=0; a<averageCount ;a++) //averages
					{
						UnitSquareMap usm = new UnitSquareMap(n, threeColors, mapDimensions);
						main.setup(usm, s);
						long start = System.currentTimeMillis();
						main.run();
						long fin = System.currentTimeMillis();
						sum += (fin-start);
					}
					Result r = main.getResults();
					r.setFinishTime(sum/averageCount);
					logger.log(r.toString() + "\n");
				}
			}
		}
	}

}
