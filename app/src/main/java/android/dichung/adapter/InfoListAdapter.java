package android.dichung.adapter;

import android.content.Context;
import android.dichung.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Vector;

import model.InformationItem;

/**
 * Created by 123456789 on 4/25/2017.
 */

public class InfoListAdapter extends BaseAdapter{
    private Vector<InformationItem> items;
    private LayoutInflater layoutInflater;
    private Context context;

    public InfoListAdapter(Context context, Vector<InformationItem> items){
        this.context = context;
        this.items = items;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_information, null);
            holder = new ViewHolder();
            holder.textTitle = (TextView) convertView.findViewById(R.id.info_title);
            holder.textInfo = (TextView) convertView.findViewById(R.id.info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        InformationItem item = this.items.get(position);
        holder.textTitle.setText(item.getTitle());
        holder.textInfo.setText(item.getInfo());

        return convertView;
    }

    static class ViewHolder {
        TextView textTitle;
        TextView textInfo;
    }
}
