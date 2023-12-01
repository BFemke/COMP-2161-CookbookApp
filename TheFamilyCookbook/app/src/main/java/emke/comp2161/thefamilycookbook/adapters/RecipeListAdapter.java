package emke.comp2161.thefamilycookbook.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import emke.comp2161.thefamilycookbook.R;
import emke.comp2161.thefamilycookbook.models.FullRecipeModel;
import emke.comp2161.thefamilycookbook.models.RecipeModel;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.MyViewHolder> {
    Context context;
    private ArrayList<FullRecipeModel> recipe_list;
    private OnRecipeListener mOnRecipeListener;

    //Constructor for RecipeAdapter
    public RecipeListAdapter(Context context, ArrayList<FullRecipeModel> recipe_list, OnRecipeListener onRecipeListener){
        this.context = context;
        this.recipe_list = recipe_list;
        this.mOnRecipeListener = onRecipeListener;
    }

    //Inflates the view using our defined layout "recipe_row"
    @NonNull
    @Override
    public RecipeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.recipe_row, parent, false);
        return new RecipeListAdapter.MyViewHolder(v, mOnRecipeListener);
    }

    //Sets up the items in the recipe array
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(recipe_list.get(position).getName());
        holder.img.setImageBitmap(recipe_list.get(position).getImg());

        //Merges all tags into a comma separated string of them
        String tag ="";
        for(int i = 0; i < recipe_list.get(position).getTags().length; i++){
            tag += recipe_list.get(position).getTags()[i];
            if((recipe_list.get(position).getTags().length-i) > 1){
                tag += ", ";
            }
        }
        holder.tags.setText(tag);
    }

    @Override
    public int getItemCount() {
        return recipe_list.size();
    }

    //Creates the view based on the contents of the recipe_list Array List
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView name;
        ImageView img;
        TextView tags;
        OnRecipeListener onRecipeListener;

        public MyViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener) {
            super(itemView);

            name = itemView.findViewById(R.id.recipeName);
            img = itemView.findViewById(R.id.recipeImg);
            tags = itemView.findViewById(R.id.recipeTags);
            this.onRecipeListener = onRecipeListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        //creates on click listeners for recycler view items
        @Override
        public void onClick(View view) {
            onRecipeListener.onRecipeClick(getAdapterPosition());
        }
        @Override
        public boolean onLongClick(View view) {
            onRecipeListener.onRecipeLongClick(getAdapterPosition());
            return true;
        }
    }

    //Interface for handling click operations back in the host activity
    public interface OnRecipeListener {
        void onRecipeClick(int position);
        void onRecipeLongClick(int position);
    }

}
