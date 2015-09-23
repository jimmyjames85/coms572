package ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jtappe on 9/23/2015.
 */
public class Search
{
    public enum SearchType
    {
        BREADTH, DEPTH, BEST, BEAM
    }


    private Page start;
    private SearchType searchType;

    //LEAF NODE - a node with no children in the search tree

    //The set of all leaf nodes available for expansion at any given point
    private Set<Page> frontier;

    //The set of all nodes already explored
    private Set<Page> explored_set;

    public Search(Page start, SearchType searchType)
    {
        this.start = start;
        this.searchType = searchType;
    }

    public void run()
    {


        bfs();
    }

    private void bfs()
    {
        frontier = new HashSet<Page>();
        explored_set = new HashSet<Page>();
    }

}
