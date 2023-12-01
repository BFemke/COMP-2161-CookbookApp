package emke.comp2161.thefamilycookbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import emke.comp2161.thefamilycookbook.R;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.MyViewHolder> {
    Context context;
    private ArrayList<String> data_list;
    private OnTagListener mOnTagListener;

    //Constructor for TagAdapter
    public TagAdapter(Context context, ArrayList<String> data_list, OnTagListener onTagListener){
        this.context = context;
        this.data_list = data_list;
        this.mOnTagListener = onTagListener;
    }

    //Inflates the view using our defined layout "form_row"
    @NonNull
    @Override
    public TagAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.form_row, parent, false);
        return new TagAdapter.MyViewHolder(v, mOnTagListener);
    }

    //Sets up the items in the data array
    @Override
    public void onBindViewHolder(@NonNull TagAdapter.MyViewHolder holder, int position) {
        holder.data.setText(data_list.get(position));

    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

    //Creates the view based on the contents
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView data;
        OnTagListener onTagListener;


        public MyViewHolder(@NonNull View itemView, OnTagListener onTagListener) {
            super(itemView);
            data = itemView.findViewById(R.id.data);
            this.onTagListener = onTagListener;

            itemView.setOnClickListener(this);
        }

        //creates on click listeners for recycler view items
        @Override
        public void onClick(View view) {
            onTagListener.onTagClick(getAdapterPosition());
        }
    }

    //Interface for handling click operations back in the host activity
    public interface OnTagListener {
        void onTagClick(int position);
    }

}
