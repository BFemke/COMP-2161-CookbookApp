package emke.comp2161.thefamilycookbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import emke.comp2161.thefamilycookbook.R;

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.MyViewHolder> {
    Context context;
    private ArrayList<String> data_list;
    private OnDirectionListener mOnDirectionListener;

    //Constructor for CategoryAdapter
    public DirectionAdapter(Context context, ArrayList<String> data_list, OnDirectionListener onDirectionListener) {
        this.context = context;
        this.data_list = data_list;
        this.mOnDirectionListener = onDirectionListener;
    }

    //Inflates the view using our defined layout "form_row"
    @NonNull
    @Override
    public DirectionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.form_row, parent, false);
        return new DirectionAdapter.MyViewHolder(v, mOnDirectionListener);
    }

    //Sets up the items in the data array
    @Override
    public void onBindViewHolder(@NonNull DirectionAdapter.MyViewHolder holder, int position) {
        int directionNum = position+1;
        holder.data.setText(directionNum+". "+data_list.get(position));
    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

    //Creates the view based on the contents
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView data;
        OnDirectionListener onDirectionListener;

        public MyViewHolder(@NonNull View itemView, OnDirectionListener onDirectionListener) {
            super(itemView);
            data = itemView.findViewById(R.id.data);
            this.onDirectionListener = onDirectionListener;

            itemView.setOnClickListener(this);
        }

        //Sets up on click listener for recycler view items
        @Override
        public void onClick(View view) {
            onDirectionListener.onDirectionClick(getAdapterPosition());
        }

    }
    //Interface for handling click operations back in the host activity
    public interface OnDirectionListener {
        void onDirectionClick(int position);
    }
}
