package android.dichung.adapter;

import android.content.Context;
import android.dichung.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

import model.SettingItem;


/**
 * Created by 123456789 on 4/20/2017.
 */

public class SettingListAdapter extends BaseAdapter {
    private Vector<SettingItem> items;
    private LayoutInflater layoutInflater;
    private Context context;

    public SettingListAdapter(Context context, Vector<SettingItem> items){
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
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.imageSetting = (ImageView) convertView.findViewById(R.id.image_setting);
            holder.textSetting = (TextView) convertView.findViewById(R.id.text_setting);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SettingItem item = this.items.get(position);
        holder.textSetting.setText(item.getItemName());

        int imageId = this.getMipmapResIdByName(item.getItemImage());

        holder.imageSetting.setImageResource(imageId);

        return convertView;
    }

    public int getMipmapResIdByName(String resName)  {
        String pkgName = context.getPackageName();

        // Trả về 0 nếu không tìm thấy.
        int resID = context.getResources().getIdentifier(resName , "drawable", pkgName);
       // Log.i("CustomListView", "Res Name: "+ resName+"==> Res ID = "+ resID);
        return resID;
    }

    static class ViewHolder {
        ImageView imageSetting;
        TextView textSetting;
    }
}
