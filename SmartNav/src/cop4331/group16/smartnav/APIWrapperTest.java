package cop4331.group16.smartnav;

import junit.framework.TestCase;
import java.util.ArrayList;

public class APIWrapperTest extends TestCase
{
	APIWrapper test = new APIWrapper();
	
//	void testDrawMap()
//	{
//		ArrayList<Address> locs = new ArrayList<Address>();
//		ArrayList<String> line = new ArrayList<String>();
//		locs.add(new Address("testStart", 0, 0));
//		locs.add(new Address("testEnd", 1, 1));
//		line.add("??_ibE_ibE");
//		test.drawMap(locs, line);
//		assert (test.locs.size() == 2);
//		assert (test.locs.get(0).getPosition().latitude == (double) 0);
//		assert (test.locs.get(0).getPosition().longitude == (double) 0);
//		assert (test.locs.get(1).getPosition().latitude == (double) 1);
//		assert (test.locs.get(1).getPosition().longitude == (double) 1);
//	}
	
	void testCalculation()
	{
		PathCalculator pathCalculator = new PathCalculator();
		APIWrapper apiWrapper = new APIWrapper();
		
		ArrayList<String> queries = new ArrayList<String>();
		queries.add("Publix");
		queries.add("Pizza");
		
		try
		{
			ArrayList<Address> path = pathCalculator.calculate(queries, new Address("Start Location", 28.6016, -81.2005));
			
			assert(path.size() == 3);
			
			Directions directions = apiWrapper.getDirections(path);
			
			RouteSection[] sections = directions.getSections();
			assert(sections[0].getStartLocation().getName().equals("Start Location"));
			assert(sections[1].getStartLocation().getName().equals("Publix Super Market at University of Palms SC"));
			assert(sections[1].getEndLocation().getName().equals("Giovanni's Italian Restaurant & Pizzeria"));
		}
		catch(Exception e)
		{
			assert(false);
		}
	}
}
