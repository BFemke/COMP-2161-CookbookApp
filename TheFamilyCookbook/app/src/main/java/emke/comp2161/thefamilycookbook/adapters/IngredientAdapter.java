package emke.comp2161.thefamilycookbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import emke.comp2161.thefamilycookbook.R;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder> {
    Context context;
    private ArrayList<String> data_list;
    private OnIngredientListener mOnIngredientListener;

    //Constructor for IngredientAdapter
    public IngredientAdapter(Context context, ArrayList<String> data_list, OnIngredientListener onIngredientListener){
        this.context = context;
        this.data_list = data_list;
        this.mOnIngredientListener = onIngredientListener;
    }

    //Inflates the view using our defined layout "form_row"
    @NonNull
    @Override
    public IngredientAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.form_row, parent, false);
        return new IngredientAdapter.MyViewHolder(v, mOnIngredientListener);
    }

    //Sets up the items in the data array
    @Override
    public void onBindViewHolder(@NonNull IngredientAdapter.MyViewHolder holder, int position) {
        holder.data.setText(data_list.get(position));
    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

    //Creates the view based on the contents
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView data;
        OnIngredientListener onIngredientListener;


        public MyViewHolder(@NonNull View itemView, IngredientAdapter.OnIngredientListener onIngredientListener) {
            super(itemView);
            data = itemView.findViewById(R.id.data);
            this.onIngredientListener = onIngredientListener;

            itemView.setOnClickListener(this);
        }

        //Sets up onclick listener for recycler view items
        @Override
        public void onClick(View view) {
            onIngredientListener.onIngredientClick(getAdapterPosition());
        }
    }

    //Interface for handling click operations back in the host activity
    public interface OnIngredientListener {
        void onIngredientClick(int position);
    }

}
