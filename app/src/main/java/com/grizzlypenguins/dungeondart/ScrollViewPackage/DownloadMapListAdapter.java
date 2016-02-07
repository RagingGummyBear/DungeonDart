package com.grizzlypenguins.dungeondart.ScrollViewPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grizzlypenguins.dungeondart.Activities.CreateMapActivity;
import com.grizzlypenguins.dungeondart.Activities.DownloadMapsFromServerActivity;
import com.grizzlypenguins.dungeondart.Activities.MainMenu;
import com.grizzlypenguins.dungeondart.R;

import java.util.ArrayList;

/**
 * Created by Darko on 07.02.2016.
 */
public class DownloadMapListAdapter extends BaseAdapter {
    ArrayList<ListInput> myList = new ArrayList<ListInput>();
    LayoutInflater inflater;
    Context context;

    public DownloadMapListAdapter(Context context, ArrayList<ListInput> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    public void update(ListInput s)
    {
        myList.add(s);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public ListInput getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return myList.get(position).get_ID();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_layout, parent, false);
            mViewHolder = new MyViewHolder(convertView,position);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        if (mViewHolder!=null) {
            ListInput currentListData = getItem(position);
            mViewHolder.mapName.setText(currentListData.mapName);
            mViewHolder.mapScore.setText(currentListData.mapScore+"");
            mViewHolder.num = currentListData.mapScore;
        }
        return convertView;
    }

    private class MyViewHolder {
        TextView mapName, mapScore;
        int num=0;
        public MyViewHolder(View item, final int n) {

            mapName = (TextView) item.findViewById(R.id.Text);
            mapScore = (TextView) item.findViewById(R.id.Time);
            // num = Integer.parseInt(mapScore.getText().toString());
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        DownloadMapsFromServerActivity temp = (DownloadMapsFromServerActivity) context;

                        if (temp != null) temp.selectMap(mapName.getText().toString(),num);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                    }


                }
            });
        }
    }
}
