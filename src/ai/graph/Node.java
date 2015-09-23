package ai.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtappe on 9/23/2015.
 */
public class Node<N>
{
    private N data;

    //Nodes that we connect to
    protected List<Node<N>> neighbors;

    public Node(N data)
    {
        this.data = data;
        neighbors = new ArrayList<Node<N>>();
    }

    public N getData()
    {
        return data;
    }

    public final List<Node<N>> getNeighbors()
    {
        return neighbors;
    }
}
