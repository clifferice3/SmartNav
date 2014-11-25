package cop4331.group16.smartnav;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;

/**
 * This class represents the map used by MapActivity.
 */
public class Map extends MapFragment 
{
	@Override
    public void onAttach(Activity activity) 
	{
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) 
    {
        return super.onCreateView(inf, parent, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);
    }
}
