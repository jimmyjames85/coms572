package ai.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtappe on 9/23/2015.
 */
public class Node<V, E>
{
	private V data;
	private Node<V, E> parent;

	//Nodes that we connect to
	protected List<E> edges;

	public Node(V data, final List<E> edges)
	{
		initNode(data, edges, null);
	}

	public Node(V data, final List<E> edges, Node<V, E> parent)
	{
		initNode(data, edges, parent);
	}

	private void initNode(V data, final List<E> edges, Node<V, E> parent)
	{
		this.data = data;

		setEdges(edges);


		this.parent = parent;
	}

	public V getData()
	{
		return data;
	}

	public final List<E> getEdges()
	{
		return edges;
	}

	public void setEdges(final List<E> edges)
	{
		this.edges = new ArrayList<E>();

		if (edges != null)
			for (E e : edges)
				this.edges.add(e);

	}


	public Node<V, E> getParentNode()
	{
		return parent;
	}

	public int getDepth()
	{
		int ret = 0;
		Node<V, E> curNode = this.parent;
		while (curNode != null)
		{
			ret++;
			curNode = curNode.parent;
		}
		return ret;
	}
}
