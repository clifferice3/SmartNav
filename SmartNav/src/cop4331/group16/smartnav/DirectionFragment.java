package cop4331.group16.smartnav;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DirectionFragment extends Fragment 
{
	
	private FragmentActivity listener;

	@Override
    public void onAttach(Activity activity) 
	{
        super.onAttach(activity);
        this.listener = (FragmentActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
    }
    ArrayAdapter<String> adapter;
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState)
    {
    	ArrayList<String> list = new ArrayList<String>();
    	adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.list_item_click_remove, R.id.text, list);
        adapter.notifyDataSetChanged();
        View view = inf.inflate(R.layout.direction_list_layout, parent, false);
        listView = (ListView) view.findViewById(R.id.directions);
        listView.setAdapter(adapter);
        return view;
    }
    
    public void updateView(ArrayList<String> dirList)
    {
    	adapter.clear();
    	adapter.addAll(dirList);
    	adapter.notifyDataSetChanged();
    	listView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);
    }
	
}
