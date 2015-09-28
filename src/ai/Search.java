package ai;

import ai.graph.Node;

import java.net.URL;
import java.util.*;

import static util.Utilities.*;

/**
 * Created by jtappe on 9/23/2015.
 */
public class Search
{
    public enum SearchType
    {
        BREADTH, DEPTH, BEST, BEAM
    }

    HeuristicFunction<Link, String> heuristicFunction;

    private Page start;
    private SearchType searchType;

    private String searchString;

    private Node<Page, Link> rootNode;
    private boolean keep_searching;
    private int num_nodes_expanded;
    //The set of all leaf nodes available for expansion at any given point
    //LEAF NODE - a node with no children in the search tree
    private Frontier<Node<Page, Link>> frontier;

    //The set of all nodes already explored
    private Set<URL> explored;

    private int beamFrontierSize = 10;

    public HeuristicFunction<Link, String> getHeuristicFunction()
    {
        return heuristicFunction;
    }

    public void setHeuristicFunction(HeuristicFunction<Link, String> heuristicFunction)
    {
        this.heuristicFunction = heuristicFunction;
    }

    public Search(Page start, SearchType searchType, String searchString)
    {
        this.start = start;
        this.searchType = searchType;
        this.searchString = searchString;
    }


    public void run()
    {

        switch (searchType)
        {
            case BREADTH:
                bfs();
                break;
            case DEPTH:
                dfs();
                break;
            case BEST:
                best();
                break;
            case BEAM:
                beam();
                break;
        }
    }


    private Boolean frontierContainsURL(URL url)
    {
        Iterator<Node<Page, Link>> itr = frontier.iterator();
        while (itr.hasNext())
        {
            if (itr.next().getData().getUrl().equals(url))
                return true;
        }
        return false;
    }

    public static Boolean INTERNAL_LINKS_ONLY = true;

    private void expandNode(Node<Page, Link> node)
    {
        //TODO the order in which we add nodes to the frontier will
        //affect the search
        List<Link> linksToAdd = null;
        explored.add(node.getData().getUrl());
        switch (searchType)
        {
            case BREADTH:
                linksToAdd = node.getData().extractLinks();
                break;
            case DEPTH:
                linksToAdd = reverseList(node.getData().extractLinks());//because it's a stack
                break;
            case BEST:
            case BEAM:
                linksToAdd = node.getData().extractLinks();
                break;
            default:
                throw new UnsupportedOperationException();
        }

        if (linksToAdd.size() == 0)
            return;

        node.setEdges(linksToAdd);

        boolean node_was_fully_explored = true;
        for (Link link : linksToAdd)
        {

            URL linkUrl = link.toURL();
            if (linkUrl != null)
            {
                //we don't want to explore pdf's or jpegs

                if (linkUrl.toExternalForm().toUpperCase().endsWith("PDF"))
                {
                    debug("Skipping URL to PDF: " + linkUrl);
                    linkUrl = null;
                } else if (linkUrl.toExternalForm().toUpperCase().endsWith("jpg"))
                {
                    debug("Skipping URL to JPG: " + linkUrl);
                    linkUrl = null;
                } else if (linkUrl.toExternalForm().toUpperCase().endsWith("jpeg"))
                {
                    debug("Skipping URL to JPEG: " + linkUrl);
                    linkUrl = null;
                } else if (node.getData().getUrl().equals(linkUrl))
                {
                    debug("Skipping URL to self: " + linkUrl);
                    linkUrl = null;
                } else if (INTERNAL_LINKS_ONLY && !linkUrl.getHost().toUpperCase().equals(node.getData().getUrl().getHost().toUpperCase()))
                {
                    //allow external .js
                    debug("Skipping URL to outside host: " + linkUrl);
                    linkUrl = null;

                }

            }

            //make sure link is not in the explored set or frontier
            //This is the diff between graph search and tree search
            if (linkUrl != null && !explored.contains(linkUrl) && !frontierContainsURL(linkUrl))
            {

                try
                {
                    Page p = new Page(linkUrl);
                    debug("Adding to frontier: " + p);
                    Node newNode = new Node<Page, Link>(p, null, node);
                    switch (searchType)
                    {

                        case DEPTH:
                        case BREADTH:
                            frontier.add(newNode);
                            break;
                        case BEST:
                        case BEAM:
                            node_was_fully_explored &= frontier.add(newNode, node.getDepth() + heuristicFunction.calculateCost(link));
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }

                } catch (IllegalArgumentException e)
                {
                    error("Unable to access link: " + link.toURL());
                    error("IllegalArgumentException: " + e);
                    e.printStackTrace();
                    System.exit(-1);
                }

            } else
            {
                //debug("Skipping link: " + link.toURL());
            }

        }

    }

    private Boolean goalTest(Node<Page, Link> node)
    {
        String pageContent = node.getData().getContent();
        if (pageContent != null)
            return pageContent.contains(this.searchString);
        else
            error("Unable to retrieve page content for goal test. " + node.getData());

        return false;
    }


    public int getBeamFrontierSize()
    {
        return beamFrontierSize;
    }

    public void setBeamFrontierSize(int beamFrontierSize)
    {
        this.beamFrontierSize = beamFrontierSize;
    }

    private String pathToNode_str(Node<Page, Link> node)
    {

        Node<Page, Link> curNode = node;
        Node<Page, Link> lastNode = curNode;
        Stack<String> displayStack = new Stack<String>();

        while (curNode != null)
        {
            int tabCount = curNode.getDepth();
            String tabs = tabs(tabCount);


            if (curNode != lastNode)
                for (Link link : curNode.getData().extractLinks())
                {
                    if (link.toURL().equals(lastNode.getData().getUrl()))
                    {
                        displayStack.push(tabs + "\tV\n");
                        displayStack.push(tabs + "\t|\n");
                        displayStack.push(tabs + link.getTitle() + "\n");
                        displayStack.push(tabs + "\t|\n");
                    }
                }

            displayStack.push(tabs + curNode.getDepth() + ":" + curNode.getData() + "\n");
            lastNode = curNode;
            curNode = curNode.getParentNode();
        }
        StringBuffer sb = new StringBuffer("");

        while (!displayStack.empty())
            sb.append((displayStack.pop()));

        return sb.toString();
    }

    private void printSuccess(Node<Page, Link> goalNode)
    {
        int numStepsToGoal = goalNode.getDepth();
        println("Discovered the node!!!");
        println(pathToNode_str(goalNode));
        debug("Frontier");
        while (!frontier.isEmpty())
        {
            Node<Page, Link> node = frontier.pop();
            debug(node.getDepth() + "\t" + node.getData().getUrl());
        }

        println("Solution path length = " + numStepsToGoal);
    }


    private void printSummary()
    {
        System.out.println(" Visited " + num_nodes_expanded + " nodes, starting @" +
                " " + start.getUrl() + ", using: " + searchType.toString() + " search.");


    }


    private void init_search()
    {

        frontier = new Frontier<Node<Page, Link>>(this.searchType);
        frontier.setBeam_frontier_max_size(this.beamFrontierSize);
        keep_searching = true;
        explored = new HashSet<URL>();
        num_nodes_expanded = 0;
    }

    private void bfs()
    {
        graph_search();
    }

    private void graph_search()
    {
        //new frontier and explored sets
        init_search();


        //initialize the explored set to be empty
        //initialize the frontier using the intial state of the problem (i.e. put the rootNode onto it)
        rootNode = new Node<Page, Link>(start, start.extractLinks());

        switch (searchType)
        {
            case DEPTH:
                frontier.add(rootNode);
                break;
            case BREADTH:
                frontier.add(rootNode);
                break;
            case BEST:
                frontier.add(rootNode, 0);
                break;
            case BEAM:
                frontier.add(rootNode, 0);

                break;
            default:

                throw new UnsupportedOperationException();
        }

        Node<Page, Link> curNode = null;

        //retrieve and remove head of queue or break the loop
        while (keep_searching && ((curNode = frontier.pop()) != null))
        {
            String parentNodeData = (curNode.getParentNode() == null) ? "<NOPARENT>" : curNode.getParentNode().getData().getUrl().toString();
            debug("[ " + parentNodeData + " ]=========> Visiting Page: " + curNode.getData().getUrl().toString());

            if (goalTest(curNode))
            {
                printSuccess(curNode);
                try
                {
                    if (System.in.read() == 'n')
                        keep_searching = false;

                } catch (Exception e)
                {
                    keep_searching = false;
                }

            } else
            {

                expandNode(curNode);
                num_nodes_expanded++;

            }
        }

        if (keep_searching)
        {
            println(" Could not find '" + searchString + "'");
            println(" Printing Explored set ...");
            printExploredSet();
        }

        printSummary();
    }

    private void printExploredSet()
    {
        for (URL url : explored)
            println("\t" + url);
    }

    private void dfs()
    {
        graph_search();
    }

    private void best()
    {
        if (this.heuristicFunction == null)
            throw new IllegalStateException("No Heuristic Function Defined ");

        graph_search();
    }

    private void beam()
    {
        best();
    }

}
