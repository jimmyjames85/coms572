package ai;

import java.util.*;
import static util.Utilities.*;
/**
 * Created by jim on 9/23/15.
 */
public class Frontier<E>
{

	private class PNode implements Comparable<PNode>
	{
		E node;
		Integer priority;

		PNode(E node, Integer priority)
		{
			this.node = node;
			this.priority = priority;
		}

		@Override
		public int compareTo(PNode that)
		{
			return this.priority.compareTo(that.priority);
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			PNode pNode = (PNode) o;

			return this.node.equals(pNode.node);
		}

		@Override
		public int hashCode()
		{
			return this.node.hashCode();
		}
	}

	private Search.SearchType searchType;

	private int beam_frontier_max_size = 10;

	private Queue<E> bfs_frontier;
	private Stack<E> dfs_frontier;
	private PriorityQueue<PNode> best_frontier;
	private PriorityQueue<PNode> beam_frontier;

	public int getBeam_frontier_max_size()
	{
		return beam_frontier_max_size;
	}

	public void setBeam_frontier_max_size(int beam_frontier_max_size)
	{
		if(beam_frontier_max_size>0)
			this.beam_frontier_max_size = beam_frontier_max_size;
	}

	public Frontier(Search.SearchType searchType)
	{
		this.searchType = searchType;
		switch (searchType)
		{
			case BREADTH:
				bfs_frontier = new ArrayDeque<E>(); //FIFO
				break;
			case DEPTH:
				dfs_frontier = new Stack<E>(); //LIFO
				break;
			case BEST:
				best_frontier = new PriorityQueue<PNode>(11, new Comparator<PNode>() {
					@Override
					public int compare(PNode pNode, PNode t1)
					{
						return pNode.priority.compareTo(t1.priority);
					}
				});
				break;
			case BEAM:
				beam_frontier = new PriorityQueue<PNode>(beam_frontier_max_size, new Comparator<PNode>() {
					@Override
					public int compare(PNode pNode, PNode t1)
					{
						return pNode.priority.compareTo(t1.priority);
					}
				});
				break;

			default:
				throw new UnsupportedOperationException();
		}
	}


	public boolean add(E item)
	{
		switch (searchType)
		{
			case BREADTH:
				return bfs_frontier.add(item);
			case DEPTH:
				dfs_frontier.push(item);
				return true;
			case BEST:
			case BEAM:
				throw new UnsupportedOperationException("Best First Search must use add(E item, int priority)");
			default:
				throw new UnsupportedOperationException();
		}
	}

	private void clean_up_beam()
	{
		if(beam_frontier.size()< beam_frontier_max_size)
			return;

		PriorityQueue<PNode> new_beam_frontier = new PriorityQueue<PNode>(beam_frontier_max_size, new Comparator<PNode>() {
			@Override
			public int compare(PNode pNode, PNode t1)
			{
				return pNode.priority.compareTo(t1.priority);
			}
		});

		int i=0;
		Iterator<PNode> itr = beam_frontier.iterator();
		while(new_beam_frontier.size()< beam_frontier_max_size && itr.hasNext())
		{
			new_beam_frontier.add(itr.next());
		}
		beam_frontier = new_beam_frontier;

	}

	public boolean add(E item, int priority)
	{
		switch (searchType)
		{
			case BREADTH:
			case DEPTH:
				return add(item);
			case BEST:
				return best_frontier.add(new PNode(item, priority));
			case BEAM:
				PNode newNode =new PNode(item, priority);
				beam_frontier.add(newNode);
				clean_up_beam();
				return beam_frontier.contains(newNode);
			default:
				throw new UnsupportedOperationException();
		}
	}

	public E peek()
	{
		switch (searchType)
		{
			case BREADTH:
				return bfs_frontier.peek();
			case DEPTH:
				return dfs_frontier.peek();
			case BEST:
				return best_frontier.peek().node;
			case BEAM:
				return beam_frontier.peek().node;
			default:
				throw new UnsupportedOperationException();
		}
	}

	public E pop()
	{
		switch (searchType)
		{
			case BREADTH:
				if(bfs_frontier.isEmpty())
					return null;
				return bfs_frontier.poll();
			case DEPTH:
				if(dfs_frontier.isEmpty())
					return null;
				return dfs_frontier.pop();
			case BEST:
				PNode p = best_frontier.poll();
				if(p!=null)
					return p.node;
				return null;
			case BEAM:
				PNode p1 = beam_frontier.poll();
				if(p1!=null)
					return p1.node;
				return null;
			default:
				throw new UnsupportedOperationException();
		}
	}


	public boolean isEmpty()
	{
		switch (searchType)
		{
			case BREADTH:
				return bfs_frontier.isEmpty();
			case DEPTH:
				return dfs_frontier.isEmpty();
			case BEST:
				return best_frontier.isEmpty();
			case BEAM:
				return beam_frontier.isEmpty();
			default:
				throw new UnsupportedOperationException();
		}
	}


	private Iterator<E> create_best_iterator()
	{
		final Iterator<PNode> itr = best_frontier.iterator();
		Iterator<E> ret = new Iterator<E>()
		{
			@Override
			public boolean hasNext()
			{
				return itr.hasNext();
			}

			@Override
			public E next()
			{
				return itr.next().node;
			}

			@Override
			public void remove()
			{
				itr.remove();
			}
		};

		return ret;
	}

	private Iterator<E> create_beam_iterator()
	{
		final Iterator<PNode> itr = beam_frontier.iterator();
		Iterator<E> ret = new Iterator<E>()
		{
			@Override
			public boolean hasNext()
			{
				return itr.hasNext();
			}

			@Override
			public E next()
			{
				return itr.next().node;
			}

			@Override
			public void remove()
			{
				itr.remove();
			}
		};

		return ret;
	}

	public Iterator<E> iterator()
	{
		switch (searchType)
		{
			case BREADTH:
				return bfs_frontier.iterator();
			case DEPTH:
				return dfs_frontier.iterator();
			case BEST:
				return create_best_iterator();
			case BEAM:
				return create_beam_iterator();
			default:
				throw new UnsupportedOperationException();
		}
	}

	public int size()
	{
		switch (searchType)
		{
			case BREADTH:
				return bfs_frontier.size();
			case DEPTH:
				return dfs_frontier.size();
			case BEST:
				return best_frontier.size();
			case BEAM:
				return beam_frontier.size();
			default:
				throw new UnsupportedOperationException();
		}
	}

	public final Search.SearchType getSearchType()
	{
		return searchType;
	}
}
