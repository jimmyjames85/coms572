package ai.provided;

// You'll need to design a Search node data structure.

// Note that the above code assumes there is a method called getHvalue()
// that returns (as a double) the heuristic value associated with a search node,
// a method called getNodeName() that returns (as a String)
// the name of the file (eg, "page7.html") associated with this node, and
// a (void) method called reportSolutionPath() that prints the path
// from the start node to the current node represented by the SearchNode instance.
public class SearchNode
{
    final String nodeName;

    public SearchNode(String name)
    {
        nodeName = name;
    }

    public void reportSolutionPath()
    {
    }

    public String getNodeName()
    {
        return nodeName;
    }
}
