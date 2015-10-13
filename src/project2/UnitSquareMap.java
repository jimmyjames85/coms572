package project2;

import aima.core.search.csp.CSP;
import aima.core.search.csp.Domain;
import aima.core.search.csp.NotEqualConstraint;
import aima.core.search.csp.Variable;
import aima.gui.applications.search.csp.CSPView;

import java.awt.*;
import java.io.*;
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
	public static final String PURPLE = "PURPLE";
	public static final String YELLOW = "YELLOW";

	private Random rand;

	private TreeSet<Point> points;
	private HashSet<Line> lines;
	private Map<Point, Variable> variables;


	private boolean threeColors = true;

	/**
	 * @param nodeTotal
	 * @param threeColors -true means 3-coloring -false means 4-coloring
	 */
	public UnitSquareMap(int nodeTotal, boolean threeColors, Rectangle mapDimensions)
	{
		if (nodeTotal < 1)
			throw new IllegalArgumentException("nodeTotal must be > 0");

		this.rand = new Random(System.currentTimeMillis());
		this.variables = new HashMap<Point, Variable>();

		createPoints(nodeTotal, mapDimensions);
		addVariables();

		setThreeColors(threeColors);
		createLines();
		addConstraints();

	}

	public UnitSquareMap(File f, boolean threeColors)
	{
		this.rand = new Random(System.currentTimeMillis());
		this.variables = new HashMap<Point, Variable>();

		loadFromFile(f);
		addVariables();
		setThreeColors(threeColors);
		addConstraints();
	}

	public void setThreeColors(boolean threeColors)
	{
		this.threeColors = threeColors;
		Domain colors = new Domain(new Object[]{RED, GREEN, BLUE});

		if (!threeColors)
			colors = new Domain(new Object[]{RED, GREEN, BLUE, PURPLE});

		for (Variable var : getVariables())
			setDomain(var, colors);

	}

	public boolean getThreeColors()
	{
		return threeColors;
	}


	public void updateCSPView(CSPView view)
	{
		view.clearMappings();
		for (Point p : points)
			view.setPositionMapping(variables.get(p), (int) p.x, (int) p.y);

		view.setColorMapping(RED, Color.RED);
		view.setColorMapping(GREEN, Color.GREEN);
		view.setColorMapping(BLUE, Color.BLUE);
		view.setColorMapping(ORANGE, Color.ORANGE);
		view.setColorMapping(PURPLE, Color.MAGENTA);
		view.setColorMapping(YELLOW, Color.YELLOW);
	}

	private void createPoints(int nodeTotal, Rectangle mapDimensions)
	{
		points = new TreeSet<Point>();

		while (points.size() != nodeTotal)
		{
			int x = rand.nextInt(mapDimensions.width) + mapDimensions.x;
			int y = rand.nextInt(mapDimensions.height) + mapDimensions.y;
			points.add(new Point(x, y));
		}

	}

	public int getNodeTotal()
	{
		return points.size();
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

		while (selection.size() > 0)
		{
			Point select = selection.get(rand.nextInt(selection.size()));
			//println("Select: "+ select);

			List<Point> distanceList = calcDistances(select);

			int i = -1;
			for (i = 0; i < distanceList.size(); i++)
			{
				Point p = distanceList.get(i);
				//println("\tconsidering: " + p);

				Line newLine = null;
				if (!p.equals(select) && !lines.contains(newLine = new Line(select, p)))
				{
					//println("\t\tnewLine= " + newLine);
					boolean intersection = false;
					for (Line l : lines)
					{
						//println("\t\t\tchecking " + l);
						//println("\t\t\t\tintersect = " + l.intersectsLine(newLine));
						intersection |= l.intersectsLine(newLine);
						if (intersection)
						{
							//println("\t\t\tintersection " + l + " and " + newLine   );
							break;
						}
					}

					if (!intersection)
					{

						lines.add(newLine);
						//println("\t\taddingNewLine " + newLine);
						break;
					}
				} else
				{
					//println("\tnot adding " +p + " " + newLine);
				}


			}

			if (i == distanceList.size())
				selection.remove(select);

		}
	}

	private void addConstraints()
	{
		for (Line l : lines)
			addConstraint(new NotEqualConstraint(variables.get(l.getStartPoint()), variables.get(l.getEndPoint())));

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

	public boolean storeToFile(File file)
	{
		PrintWriter pw = null;
		try
		{
			pw = new PrintWriter(file, "UTF-8");

			pw.println("POINTS");

			for (Point p : this.points)
				pw.printf("%f,%f\n", p.x, p.y);

			pw.println("LINES");

			for (Line l : this.lines)
				pw.printf("%f,%f,%f,%f\n", l.s.x, l.s.y, l.t.x, l.t.y);

			pw.close();
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
		finally
		{
			if (pw != null)
				pw.close();
		}
	}

	private Point readPoint(String str)
	{
		int c = str.indexOf(',');
		if (c == -1)
			return null;

		double x = Double.parseDouble(str.substring(0, c));
		double y = Double.parseDouble(str.substring(c + 1));


		return new Point(x, y);
	}

	private Line readLine(String str)
	{
		int c = str.indexOf(',');
		if(c==-1)
			return null;

		c = str.indexOf(',',c+1);
		if(c==-1)
			return null;

		Point s = readPoint(str.substring(0, c));
		if (s == null)
			return null;

		Point t = readPoint(str.substring(c + 1));

		if (t == null)
			return null;

		return new Line(s, t);
	}

	private void loadFromFile(File file)
	{
		int mode = 0;
		points = new TreeSet<Point>();
		lines = new HashSet<Line>();

		FileReader in = null;
		try
		{
			in = new FileReader(file);

			BufferedReader br = new BufferedReader(in);

			String str;

			while ((str = br.readLine()) != null)
			{
				if (str.equals("POINTS"))
					mode = 1;
				else if (mode == 1 && str.equals("LINES"))
					mode = 2;
				else if (mode == 1)
				{
					Point p = readPoint(str);
					if (p != null)
						points.add(p);
				} else if (mode == 2)
				{
					Line l = readLine(str);
					if (l != null)
						lines.add(l);
				}
			}



		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Error parsing file<"+file+">: " + e.getMessage());
		}
		finally
		{
			if (in != null)
				try
				{
					in.close();
				}
				catch (Exception ee)
				{
				}
		}

	}

	public static void main(String args[])
	{
		/*Line l1 = new Line(new Point(6, 6), new Point(10, 10));
		Line l2 = new Line(new Point(0, 10), new Point(10, 0));

		File f = new File("graph.txt");

		//UnitSquareMap usm = new UnitSquareMap(10,true, new Rectangle(50,50));
		//usm.storeToFile(f);
		UnitSquareMap usm = new UnitSquareMap(f, true);
*/

		//println(l1.intersectsLine(l2));
		//println("y = " + l2.getSlope() + " X  +  " + l2.getYIntercept());

		//new UnitSquareMap(10, true);//, new Rectangle(0, 0, 10, 10));
	}
}
