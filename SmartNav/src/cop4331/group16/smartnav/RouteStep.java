package cop4331.group16.smartnav;

public class RouteStep
{
	private String htmlInstructions;
	private String polyline;
	
	public RouteStep(String instructions, String line)
	{
		htmlInstructions = instructions;
		polyline = line;
	}
	
	public String getHtmlInstructions()
	{
		return htmlInstructions;
	}
	
	public String getPolyline()
	{
		return polyline;
	}
}
