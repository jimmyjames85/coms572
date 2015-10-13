package project2;

/**
 * Created by jim on 10/10/15.
 */

public class Point implements Comparable<Point>
{
	protected double x;
	protected double y;

	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	@Override
	public int compareTo(Point o)
	{
		if (o == null)
			return Integer.MIN_VALUE;

		if (this.y < o.y)
			return -1;
		if (this.y > o.y)
			return +1;
		if (this.x < o.x)
			return -1;
		if (this.x > o.x)
			return +1;

		return 0;
	}

	private static final double ERR = .000001;
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Point point = (Point) o;
		return (Math.abs(point.x - x)< ERR) && (Math.abs(point.y - y)< ERR);


	}

	@Override
	public String toString()
	{
		return "(" + (int) x + "," + (int) y + ")";
	}


	public static Double calculateDistance(Point x, Point y)
	{
		double dx = x.x - y.x;
		double dy = x.y - y.y;

		return Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public int hashCode()
	{
		int result;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
