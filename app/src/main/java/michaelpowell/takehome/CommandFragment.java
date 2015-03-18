package michaelpowell.takehome;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CommandFragment extends ListFragment {

  private CommandAdapter mAdapter;
  private CommandSelectedListener mListener;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mListener = (CommandSelectedListener) activity;
  }

  interface CommandSelectedListener {
    public void onCommandChecked(Command command, boolean checked);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_command, container, false);
    mAdapter = new CommandAdapter(getActivity(), new ArrayList<Command>());
    return rootView;
  }

  @Override
  public void onResume() {
    super.onResume();
    getListView().setAdapter(mAdapter);
    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
  }

  public void addCommand(Command command) {
    mAdapter.addCommand(command);
  }

  public void clearCommands() {
  }

  private class CommandAdapter extends ArrayAdapter<Command> implements View.OnClickListener {

    private Context mContext;
    public List<Command> mCommandList;
    public List<Boolean> mIsCheckedList;

    public CommandAdapter(Context context, List<Command> commandList) {
      super(context, R.layout.list_item_command, commandList);
      mContext = context;
      mCommandList = commandList;
      mIsCheckedList = new ArrayList<>();
    }

    public void addCommand(Command command) {
      mCommandList.add(command);
      mIsCheckedList.add(true);
      this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = convertView;
      ViewHolder holder;
      if (view == null) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_item_command, parent, false);
        holder = new ViewHolder();
        holder.text = (TextView) view.findViewById(R.id.text);
        view.setTag(holder);
      }
      Command command = mCommandList.get(position);
      holder = (ViewHolder) view.getTag();
      holder.text.setText(command.toString());
      view.setTag(R.id.position_tag, position);
      view.setOnClickListener(this);
      return view;
    }

    @Override
    public void onClick(View v) {
      int position = (int) v.getTag(R.id.position_tag);
      boolean isChecked = mIsCheckedList.get(position);
      mIsCheckedList.set(position, !isChecked);
      mListener.onCommandChecked(mCommandList.get(position), !isChecked);
    }

  }
    private static class ViewHolder {
      TextView text;
    }

}
