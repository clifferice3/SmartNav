package cop4331.group16.smartnav;

public class Directions
{
	private RouteSection[] sections;
	private Address southwest;
	private Address northeast;
	
	public Directions(RouteSection[] s, Address sw, Address ne)
	{
		sections = s;
		southwest = sw;
		northeast = ne;
	}
	
	public RouteSection[] getSections()
	{
		return sections;
	}
	
	public Address getSouthwest()
	{
		return southwest;
	}
	
	public Address getNortheast()
	{
		return northeast;
	}
}
