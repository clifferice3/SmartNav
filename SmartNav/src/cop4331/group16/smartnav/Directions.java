package cop4331.group16.smartnav;

/**
 * This class stores all the information about directions needed by the app
 */
public class Directions
{
	private RouteSection[] sections;	// Sections of the route
	private Address southwest;			// Southwest corner of bounding box
	private Address northeast;			// Northeast corner of bounding box
	
	// Constructor
	public Directions(RouteSection[] s, Address sw, Address ne)
	{
		sections = s;
		southwest = sw;
		northeast = ne;
	}
	
	// Getter methods
	
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
