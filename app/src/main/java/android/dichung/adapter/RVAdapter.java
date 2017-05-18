package android.dichung.adapter;

import android.app.Activity;
import android.dichung.R;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

import Modules.Converter;
import model.Student;

/**
 * Created by 123456789 on 5/8/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.StudentViewHolder> {

    private Vector<Student> students;
    private Activity activity;
    private int res;

    public OnItemClickRVrecycleViewListener onItemClickRVrecycleViewListener;

    public interface OnItemClickRVrecycleViewListener {
        void onItemClick(View itemview, int position);
    }

    public RVAdapter(Vector<Student> students, Activity activity, int res, OnItemClickRVrecycleViewListener onItemClickRV) {
        this.students = students;
        this.activity = activity;
        this.res = res;
        onItemClickRVrecycleViewListener = onItemClickRV;
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(res, null);
        StudentViewHolder studentViewHolder = new StudentViewHolder(v);
        return studentViewHolder;
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
        holder.onItemClickRVrecycleViewListener = onItemClickRVrecycleViewListener;

        Bitmap bitmap = Converter.stringToBitmap(students.get(position).getImageCode());
        holder.imgvPicture.setImageBitmap(bitmap);
        holder.tvName.setText(students.get(position).getName());
        holder.tvGender.setText(students.get(position).getGender());
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {

        public OnItemClickRVrecycleViewListener onItemClickRVrecycleViewListener;

        public CardView cv;
        public ImageView imgvPicture;
        public TextView tvName;
        public TextView tvGender;

        public StudentViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickRVrecycleViewListener.onItemClick(view, getLayoutPosition());
                }
            });
            cv = (CardView) itemView.findViewById(R.id.cvCarView);
            imgvPicture = (ImageView) itemView.findViewById(R.id.ivCardViewPicture);
            tvName = (TextView) itemView.findViewById(R.id.tvCardViewName);
            tvGender = (TextView) itemView.findViewById(R.id.tvCardViewGender);
        }
    }

}
