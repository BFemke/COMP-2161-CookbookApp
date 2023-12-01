package emke.comp2161.thefamilycookbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import emke.comp2161.thefamilycookbook.CategoryActivity;
import emke.comp2161.thefamilycookbook.models.CategoryModel;
import emke.comp2161.thefamilycookbook.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{
    Context context;
    private List<CategoryModel> category_list;
    private OnCategoryListener mOnCategoryListener;

    //Constructor for CategoryAdapter
    public CategoryAdapter(Context context, List<CategoryModel> category_list, OnCategoryListener onCategoryListener){
        this.context = context;
        this.category_list = category_list;
        this.mOnCategoryListener = onCategoryListener;
    }

    //Inflates the view using our defined layout "category_row"
    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.category_row, parent, false);
        return new CategoryAdapter.MyViewHolder(v, mOnCategoryListener);
    }

    //Sets up the items in the category array
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, int position) {
        holder.name.setText(category_list.get(position).getName());
        holder.img.setImageBitmap(category_list.get(position).getImg());
    }

    @Override
    public int getItemCount() {
        return category_list.size();
    }

    //Creates the view based on the contents of the category_list Array List
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView name;
        ImageView img;
        CardView card;
        OnCategoryListener onCategoryListener;

        public MyViewHolder(@NonNull View itemView, OnCategoryListener onCategoryListener) {
            super(itemView);

            name = itemView.findViewById(R.id.categoryName);
            img = itemView.findViewById(R.id.categoryImage);
            card = itemView.findViewById(R.id.categoryCard);
            this.onCategoryListener = onCategoryListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        //Sets up click listener for recycler itemss
        @Override
        public void onClick(View view) {
            onCategoryListener.onCategoryClick(getAdapterPosition());
        }
        @Override
        public boolean onLongClick(View view) {
            onCategoryListener.onCategoryLongClick(getAdapterPosition());
            return true;
        }
    }

    //Interface for handling click operations back in the host activity
    public interface OnCategoryListener {
        void onCategoryClick(int position);
        void onCategoryLongClick(int position);
    }

}
