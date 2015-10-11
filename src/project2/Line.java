package project2;

import static util.Utilities.*;

/**
 * Created by jim on 10/10/15.
 */
public class Line
{
	private Point s;
	private Point t;
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

		this.yIntercept =  s.y - slope * s.x;
	}

	public Point calculateIntersection(Line o)
	{
		if (o == null)
			throw new IllegalArgumentException("Null point error!");


		if(this.getEndPoint().equals(o.getEndPoint()))
			return o.getEndPoint();

		if(this.getStartPoint().equals(o.getStartPoint()))
			return o.getStartPoint();

		Double dm;
		if (o.slope == null || this.slope == null || (dm = (this.slope - o.slope)) == 0)
		{
			if(o.slope == null)
			{
				if(this.slope == null)
					return null;

				return new Point( o.s.x, (int)(this.slope*o.s.x + this.yIntercept));
			}
			else
			{
				return new Point( s.x, (int)(o.slope*s.x + o.yIntercept));
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

		println (o);
		println (this);

		println(minX() +"<"+intersection.x+"<"+maxX());
		println(o.minX() +"<"+intersection.x+"<"+o.maxX());

		println(minY() +"<"+intersection.y+"<"+maxY());
		println(o.minY() +"<"+intersection.y+"<"+o.maxY());


		println((o.minX() < intersection.x && intersection.x < o.maxX()));
		println(minX() < intersection.x && intersection.x < maxX());
		println(o.minY() < intersection.y && intersection.y < o.maxY());
		println(minY() < intersection.y && intersection.y < maxY());

		return ( (o.minX() < intersection.x && intersection.x < o.maxX()) &&
				(minX() < intersection.x && intersection.x < maxX()) &&
				(o.minY() < intersection.y && intersection.y < o.maxY()) &&
				 (minY() < intersection.y && intersection.y < maxY()));
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

		String msg="y = " + getSlope() + "(x) + " + yIntercept;


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
		Line l1 = new Line(new Point(6,13), new Point(11,26));
		Line l2 = new Line(new Point(6,13), new Point(13,25));

		System.out.println(l1.intersectsLine(l2));
	}
}


