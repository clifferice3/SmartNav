package cop4331.group16.smartnav;

import junit.framework.TestCase;
import java.util.ArrayList;

public class APIWrapperTest extends TestCase
{
	APIWrapper test = new APIWrapper();
	
	void testDrawMap()
	{
		ArrayList<Address> locs = new ArrayList<Address>();
		locs.add(new Address("testStart", 0, 0));
		locs.add(new Address("testEnd", 1, 1));
		test.drawMap(locs, "??_ibE_ibE");
		assert (test.locs.size() == 2);
		assert (test.locs.get(0).getPosition().latitude == (double) 0);
		assert (test.locs.get(0).getPosition().longitude == (double) 0);
		assert (test.locs.get(1).getPosition().latitude == (double) 1);
		assert (test.locs.get(1).getPosition().longitude == (double) 1);
	}
}
