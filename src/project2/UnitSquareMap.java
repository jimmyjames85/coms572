package project2;

import aima.core.search.csp.CSP;
import aima.core.search.csp.NotEqualConstraint;
import aima.core.search.csp.Variable;

import static util.Utilities.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by jim on 10/10/15.
 */
public class UnitSquareMap extends CSP
{

	public static final String RED = "RED";
	public static final String GREEN = "GREEN";
	public static final String BLUE = "BLUE";
	public static final String ORANGE = "ORANGE";

	private Random rand;
	private boolean threeColors;
	private int nodeTotal;

	private TreeSet<Point> points;
	private HashSet<Line> lines;
	private Map<Point, Variable> variables;

	private Rectangle mapDimensions;

	/**
	 * @param nodeTotal
	 * @param threeColors -true means 3-coloring -false means 4-coloring
	 */
	public UnitSquareMap(int nodeTotal, boolean threeColors, Rectangle mapDimensions)
	{

		if (nodeTotal < 1)
			throw new IllegalArgumentException("nodeTotal must be > 0");

		this.nodeTotal = nodeTotal;
		this.threeColors = threeColors;
		this.mapDimensions = mapDimensions;

		this.rand = new Random(System.currentTimeMillis());
		this.variables = new HashMap<Point, Variable>();

		createPoints(this.nodeTotal);
		addVariables();
		createLines();
	}

	private void createPoints(int nodeTotal)
	{
		points = new TreeSet<Point>();

		while (points.size() != nodeTotal)
		{
			int x = rand.nextInt(mapDimensions.width) + mapDimensions.x;
			int y = rand.nextInt(mapDimensions.height) + mapDimensions.y;
			points.add(new Point(x, y));
		}

	}

	private void addVariables()
	{
		for (Point p : points)
		{

			Variable v = new Variable(p.toString());
			addVariable(v);

			variables.put(p, v);
		}
	}

	private void createLines()
	{
		this.lines = new HashSet<Line>();


		ArrayList<Point> selection = new ArrayList<Point>();
		for (Point p : points)
			selection.add(p);


		while (selection.size()>0)
		{
			Point select = selection.get(rand.nextInt(selection.size()));
			List<Point> distanceList = calcDistances(select);

			int i = -1;
			for (i = 0; i < distanceList.size(); i++)
			{
				Point p = distanceList.get(i);
				if (!p.equals(select))
				{
					Line newLine = new Line(select, p);
					boolean noIntersection = true;

					if (!lines.contains(newLine))
					{
						for (Line l : lines)
						{
							noIntersection &= l.intersectsLine(newLine);
							if (noIntersection)
								break;
						}
					}

					if (noIntersection)
					{
						lines.add(newLine);
						break;
					}
				}
			}
			selection.remove(select);
		}
	}

	private void addConstraints()
	{
		for(Line l: lines)
			addConstraint(new NotEqualConstraint(variables.get(l.getStartPoint()),variables.get(l.getEndPoint())));

	}

	private class Pair
	{
		Point p;
		double d;

		Pair(Point p, double d)
		{
			this.p = p;
			this.d = d;
		}

	}

	private List<Point> calcDistances(Point select)
	{
		if (!points.contains(select))
			return null;

		ArrayList<Pair> list = new ArrayList<Pair>();

		for (Point p : points)
			list.add(new Pair(p, Point.calculateDistance(select, p)));

		Object[] sorted = list.toArray();
		java.util.Arrays.sort(sorted, new Comparator<Object>()
		{
			@Override
			public int compare(Object o, Object t1)
			{
				Pair p1 = (Pair) o;
				Pair p2 = (Pair) t1;

				if (p1.d < p2.d)
					return -1;
				else if (p1.d > p2.d)
					return 1;

				return 0;

			}
		});

		ArrayList<Point> sortedList = new ArrayList<Point>();
		for (int i = 0; i < sorted.length; i++)
			sortedList.add(((Pair) sorted[i]).p);

		return sortedList;
	}


	public static void main(String args[])
	{
		Line l1 = new Line(new Point(6, 6), new Point(10, 10));
		Line l2 = new Line(new Point(0, 10), new Point(10, 0));

		println(l1.intersectsLine(l2));
		println("y = " + l2.getSlope() + " X  +  " + l2.getYIntercept());

		new UnitSquareMap(10, true, new Rectangle(0, 0, 10, 10));
	}
}
