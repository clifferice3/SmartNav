package cop4331.group16.smartnav;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.Arrays;

public class InputActivity extends ListActivity implements OnScrollListener {

  private ArrayList<String> list;
  private ArrayAdapter<String> adapter;
  private DragSortListView dragSortListView;
  private DragSortController dragSortListViewController;

  private Button editButton;
  private Button addItemButton;
  private Button submitButton;
  private Button clearAllButton;

  private int removeMode = DragSortController.CLICK_REMOVE;
  private int dragStartMode = DragSortController.ON_DOWN;
  private boolean removeEnabled = true;
  private boolean sortEnabled = true;
  private boolean dragEnabled = true;
  private boolean editModeEnabled = false;

  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    setContentView(getLayout());
    getActionBar().setTitle("SmartNav - Optimal To-Do Navigator");
    getActionBar().show();
    
    dragSortListView = (DragSortListView) getListView();
    dragSortListViewController = buildController(dragSortListView);
    dragSortListView.setFloatViewManager(dragSortListViewController);
    dragSortListView.setOnTouchListener(dragSortListViewController);
    dragSortListView.setDragEnabled(dragEnabled);
    dragSortListView.setDropListener(onDrop);
    dragSortListView.setRemoveListener(onRemove);

    setListAdapter();

    getListView().setOnScrollListener(this);
    
    list = new ArrayList<String>();
    editButton = (Button) findViewById(R.id.editButton);
    addItemButton = (Button) findViewById(R.id.addButton);
    submitButton = (Button) findViewById(R.id.submitButton);
    clearAllButton = (Button) findViewById(R.id.clearAllButton);

    editButton.setOnClickListener(new OnClickListener() {
      public void onClick(View view) {
        if (adapter.getCount() == 0)
          return;
        
        editModeEnabled = !editModeEnabled;
        if (editModeEnabled) {
          editButton.setText(R.string.editButtonDone);
        } else {
          editButton.setText(R.string.editButton);
        }
        updateVisibility();
      }
    });

    submitButton.setOnClickListener(new OnClickListener() {
      public void onClick(View view) {
        Intent nextAct = new Intent(InputActivity.this, MapActivity.class);
        nextAct.putStringArrayListExtra("input_list", list);
        startActivity(nextAct);
      }
    });

    addItemButton.setOnClickListener(new OnClickListener() {
      public void onClick(View view) {
        final EditText addItemEditText = new EditText(InputActivity.this);
        addItemEditText.setSingleLine(true);
        
        new AlertDialog.Builder(InputActivity.this).setTitle("Add New To-Do Item")
            .setMessage("Enter in the location/task you need to complete:")
            .setView(addItemEditText)
            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                addItem(addItemEditText.getText().toString());
              }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {}
            }).show();
      }
    });

    clearAllButton.setOnClickListener(new OnClickListener() {
      public void onClick(View view) {
        adapter.clear();
        list.clear();
        adapter.notifyDataSetChanged();

      }
    });

    ListView listView = getListView();
    listView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> adapterview, View view, int position, long rowId) {
        
      }
    });

    listView.setOnItemLongClickListener(new OnItemLongClickListener() {
      public boolean onItemLongClick(AdapterView<?> adapterview, View view, int position, long rowId) {
         return true;
      }
    });
  }

  private void updateVisibility() {
    ListView listView = getListView();
    for (int i = 0; i < listView.getChildCount(); i++) {
      View itemView = listView.getChildAt(i);
      itemView.findViewById(R.id.drag_handle).setVisibility(
          editModeEnabled ? View.VISIBLE : View.GONE);
      itemView.findViewById(R.id.click_remove).setVisibility(
          editModeEnabled ? View.VISIBLE : View.GONE);
    }
    
    findViewById(R.id.editButton).setVisibility(listView.getChildCount() > 0 ? View.VISIBLE : View.INVISIBLE);
    findViewById(R.id.clearAllButton).setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
    findViewById(R.id.submitButton).setVisibility(editModeEnabled ? View.GONE : View.VISIBLE);
}

  private void addItem(String newItem) {
    if (newItem.length() > 0) {
      adapter.add(newItem);
      list.add(newItem);
      updateVisibility();
      dragSortListView.smoothScrollToPosition(adapter.getCount() - 1);
    }
  }

  public DragSortController buildController(DragSortListView dragsortlistview) {
    DragSortController dragsortcontroller = new DragSortController(dragsortlistview);
    dragsortcontroller.setDragHandleId(R.id.drag_handle);
    dragsortcontroller.setClickRemoveId(R.id.click_remove);
    dragsortcontroller.setRemoveEnabled(removeEnabled);
    dragsortcontroller.setSortEnabled(sortEnabled);
    dragsortcontroller.setDragInitMode(dragStartMode);
    dragsortcontroller.setRemoveMode(removeMode);
    return dragsortcontroller;
  }

  public DragSortController getController() {
    return dragSortListViewController;
  }

  protected int getItemLayout() {
    return R.layout.list_item_click_remove;
  }

  protected int getLayout() {
    return R.layout.dslv_fragment_layout;
  }

  public void setListAdapter() {
    list = new ArrayList<String>();
    adapter = new ArrayAdapter<String>(this, getItemLayout(), R.id.text, list);
    setListAdapter(adapter);
  }

  private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
    public void drop(int from, int to) {
      if (from != to) {
        String item = adapter.getItem(from);
        adapter.remove(item);
        adapter.insert(item, to);
        list.remove(item);
        list.add(to, item);
        updateVisibility();
      }
    }
  };

  private DragSortListView.RemoveListener onRemove =
      new com.mobeta.android.dslv.DragSortListView.RemoveListener() {
        public void remove(int item) {
          adapter.remove(adapter.getItem(item));
          list.remove(list.get(item));
          updateVisibility();
        }
      };

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
    updateVisibility();
  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
      int totalItemCount) {
    updateVisibility();
  }
}
