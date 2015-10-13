package project2;

import static util.Utilities.*;

/**
 * Created by jim on 10/10/15.
 */
public class Line
{
	protected Point s;
	protected Point t;
	private Double slope;
	private Double yIntercept;

	public Line(Point s, Point t)
	{
		if (s == null || t == null)
			throw new IllegalArgumentException("Null point error!");

		int cmp = s.compareTo(t);


		this.s = s;
		this.t = t;

		if (cmp > 0)
		{
			this.s = t;
			this.t = s;
		} else if (cmp == 0)
		{
			throw new IllegalArgumentException("The points do not create a line! ");
		}


		this.slope = calculateSlope(s, t);
		calculateYIntercept();

	}


	public Point getStartPoint()
	{
		return s;
	}

	public Point getEndPoint()
	{
		return t;
	}

	public Double getSlope()
	{
		return slope;
	}

	public Double getYIntercept()
	{
		return yIntercept;
	}

	public double minX()
	{
		return Math.min(s.x, t.x);
	}

	public double maxX()
	{
		return Math.max(s.x, t.x);
	}

	public double minY()
	{
		return Math.min(s.y, t.y);
	}

	public double maxY()
	{
		return Math.max(s.y, t.y);
	}

	private void calculateYIntercept()
	{
		if (slope == null)
			return;

		this.yIntercept = s.y - slope * s.x;
	}

	public Point calculateIntersection(Line o)
	{
		if (o == null)
			throw new IllegalArgumentException("Null point error!");


		if (this.getEndPoint().equals(o.getEndPoint()))
			return o.getEndPoint();

		if (this.getStartPoint().equals(o.getStartPoint()))
			return o.getStartPoint();

		Double dm;
		if (o.slope == null || this.slope == null || (dm = (this.slope - o.slope)) == 0)
		{
			if (o.slope == null)
			{
				if (this.slope == null)
					return null;

				return new Point(o.s.x, (this.slope * o.s.x + this.yIntercept));
			} else
			{
				return new Point(s.x, (o.slope * s.x + o.yIntercept));
			}
		}


		Double yint = this.yIntercept;
		Double db = o.yIntercept - yint;


		double x = (db / dm);
		double y = (slope * x + yint);


		return new Point(x, y);
	}

	public Boolean intersectsLine(Line o)
	{
		if (o == null)
			throw new IllegalArgumentException("Null line error!");

		Point intersection = calculateIntersection(o);
		if (intersection == null)
			return false;
/*
		println (o);
		println (this);

		println(o.minX() +"<"+intersection.x+"<"+o.maxX() + "= " + (((o.minX() < intersection.x) && (intersection.x < o.maxX()))));
		println("o.minX = " + o.minX() + " o.maxX = " + o.maxX() + "  intersection.x = " + intersection.x + " equal? = " + ( o.minX()==o.maxX() && o.minX()==intersection.x));
		println("");
		println(minX() +"<"+intersection.x+"<"+maxX() + "= " + (minX() < intersection.x && intersection.x < maxX()));
		println("  minX = " + minX() +   "   maxX = " +maxX() +   "  intersection.x = " + intersection.x + " equal? = " + (minX()==maxX() && minX() ==intersection.x));
		println("");
		println(o.minY() +"<" + intersection.y+"<"+o.maxY() + "= " + (o.minY() < intersection.y && intersection.y < o.maxY()));
		println("o.minY = " + o.minY() + " o.maxY = " +o.maxY() + "  intersection.y = " + intersection.y + " equal? = " + ( o.minY()==o.maxY() && o.minY()==intersection.y));
		println("");
		println(minY() +"<"+intersection.y+"<"+maxY() + "= " + (minY() < intersection.y && intersection.y < maxY()));
		println("  minY = " +   minY() + "   maxY = "  + maxY() + "  intersection.y = " + intersection.y + " equal? = " + (   minY()==  maxY() && minY()==intersection.y));
		println("");
*/


		return (((o.minX() < intersection.x && intersection.x < o.maxX()) || (o.minX() == o.maxX() && o.minX() == intersection.x)) &&
				((minX() < intersection.x && intersection.x < maxX()) || (minX() == maxX() && minX() == intersection.x)) &&
				((o.minY() < intersection.y && intersection.y < o.maxY()) || (o.minY() == o.maxY() && o.minY() == intersection.y)) &&
				((minY() < intersection.y && intersection.y < maxY()) || (minY() == maxY() && minY() == intersection.y)));
	}

	public static Double calculateSlope(Point s, Point t)
	{
		double dx = s.x - t.x;
		double dy = s.y - t.y;

		if (dx != 0)
			return dy / dx;

		return null;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Line line = (Line) o;


		if (s != null ? !s.equals(line.s) : line.s != null)
			return false;
		return !(t != null ? !t.equals(line.t) : line.t != null);

	}

	@Override
	public String toString()
	{

		String msg = "y = " + getSlope() + "(x) + " + yIntercept;


		return "Line{" +
				"s=" + s +
				", t=" + t +
				'}' + msg;
	}

	@Override
	public int hashCode()
	{
		int result = s.hashCode();
		result = 31 * result + t.hashCode();
		return result;
	}

	public static void main(String args[])
	{
		Line l1 = new Line(new Point(0, 17), new Point(8, 25));
		Line l2 = new Line(new Point(2, 36), new Point(3, 36));
		System.out.println(l2.intersectsLine(l1));
	}
}


