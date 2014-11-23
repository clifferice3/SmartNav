package cop4331.group16.smartnav;

/**
 * This class stores information about a single step of the path
 */
public class RouteStep
{
	private String htmlInstructions;	// html instructions about this step
	private String polyline;			// Encoded polyline to be places on the map for this step
	
	// Constructor
	public RouteStep(String instructions, String line)
	{
		htmlInstructions = instructions;
		polyline = line;
	}
	
	// Getter methods
	
	public String getHtmlInstructions()
	{
		return htmlInstructions;
	}
	
	public String getPolyline()
	{
		return polyline;
	}
}
