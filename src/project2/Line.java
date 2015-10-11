package project2;

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
		}
		else if(cmp==0)
		{
			throw new IllegalArgumentException("The points do not create a line! ");
		}


		this.slope = calculateSlope(s, t);
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

	public Double minX()
	{
		return Math.min(s.x, t.x);
	}

	public Double maxX()
	{
		return Math.max(s.x, t.x);
	}

	public Double minY()
	{
		return Math.min(s.y, t.y);
	}

	public Double maxY()
	{
		return Math.max(s.y, t.y);
	}

	public Double getYIntercept()
	{
		if (slope == null)
			return null;

		return s.y - slope * s.x;
	}

	public Point calculateIntersection(Line o)
	{
		if (o == null)
			throw new IllegalArgumentException("Null point error!");

		Double dm;
		if (o.slope == null || this.slope == null || (dm = (this.slope - o.slope)) == 0)
			return null;

		Double yint = this.getYIntercept();
		Double db = o.getYIntercept() - yint;


		Double x = db / dm;
		Double y = slope * x + yint;

		return new Point(x, y);
	}

	public Boolean intersectsLine(Line o)
	{
		if (o == null)
			throw new IllegalArgumentException("Null line error!");

		Point intersection = calculateIntersection(o);
		if (intersection == null)
			return false;

		return (o.minX() <= intersection.x && intersection.x <= o.maxX()) &&
				(minX() <= intersection.x && intersection.x <= maxX()) &&
				(o.minY() <= intersection.y && intersection.y <= o.maxY()) &&
				(minY() <= intersection.y && intersection.y <= maxY());
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
		return "Line{" +
				"s=" + s +
				", t=" + t +
				'}';
	}

	@Override
	public int hashCode()
	{
		int result = s.hashCode();
		result = 31 * result + t.hashCode();
		return result;
	}
}


