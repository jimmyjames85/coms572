package project2;

import aima.core.agent.Action;
import aima.core.agent.Agent;
import aima.core.agent.EnvironmentState;
import aima.core.agent.EnvironmentView;
import aima.core.search.csp.*;
import aima.core.util.datastructure.FIFOQueue;
import aima.gui.applications.search.csp.CSPEnvironment;
import aima.gui.framework.MessageLogger;


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

    public Project2Main(UnitSquareMap usm, MessageLogger logger)
    {
        this.usm = usm;
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

    public void setup(SearchStrategy searchStrategy)
    {
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
                    strategy = new ImprovedBacktrackingStrategy
                            (false, false, true, false);
                    break;
                case MRV_DEG_AC3_LCV: // MRV + DEG + AC3 + LCV
                    strategy = new ImprovedBacktrackingStrategy
                            (true, true, true, true);
                    break;
                case MIN_CONFLICTS:
                    strategy = new MinConflictsStrategy(50);
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
                        actions.add(new CSPEnvironment.StateChangeAction(
                                assignment, csp));
                    }

                    @Override
                    public void stateChanged(CSP csp)
                    {
                        actions.add(new CSPEnvironment.StateChangeAction(
                                csp));
                    }
                });
                strategy.solve(env.getCSP().copyDomains());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public String getStateString()
    {
        StringBuilder sb = new StringBuilder("[ ");
        if (env == null)
            return "[No assignments yet]";
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
                sb.append(value);
            else
                sb.append("(???)");

            if (i < vars.size() - 1)
                sb.append(", ");
        }
        sb.append(" ]");
        return sb.toString();
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

        int nodeTotal = 50;
        boolean threeColors = true; //<-----------TODO

        UnitSquareMap usm = new UnitSquareMap(nodeTotal, threeColors);
        Project2Main main = new Project2Main(usm, logger);
        //main.setVerbosity(true);

        for (int i = 0; i < nodeTotal; i++)
        {
            for (SearchStrategy s : SearchStrategy.values())
            {
                main.setup(s);
                long start = System.currentTimeMillis();
                main.run();
                long fin = System.currentTimeMillis();
                double seconds = (fin - start) / 1000;
                logger.log(s.toString() + ": " + seconds + " ms\n\t");
                logger.log(main.getStateString());
                logger.log("\n\n");
            }
        }
    }

}
