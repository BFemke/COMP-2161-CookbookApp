package emke.comp2161.thefamilycookbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import emke.comp2161.thefamilycookbook.adapters.RecipeListAdapter;
import emke.comp2161.thefamilycookbook.models.FullRecipeModel;
import emke.comp2161.thefamilycookbook.models.RecipeModel;

public class CategoryActivity extends AppCompatActivity implements RecipeListAdapter.OnRecipeListener {

    private TextView cat_name;
    private String categoryName;
    private ImageButton settingsBtn;
    private ImageButton backBtn;
    private ImageButton addRecipeBtn;
    private RecipeListAdapter adapter;
    private ArrayList<RecipeModel> recipes = new ArrayList<>();
    private ArrayList<FullRecipeModel> mRecipes = new ArrayList<>();
    private Gson gson = new Gson();
    private RecyclerView recipe_list;
    private int theme;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets theme of activity as the stored theme
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("currentTheme", R.style.Theme_TheFamilyCookbook);
        setTheme(theme);
        setContentView(R.layout.activity_category);

        recipe_list = findViewById(R.id.recipeRecycler);
        addRecipeBtn = findViewById(R.id.add_category);
        cat_name = findViewById(R.id.settings_heading);
        backBtn = findViewById(R.id.setting_back_btn);
        settingsBtn = findViewById(R.id.recipeSettingsBtn);

        //Gets intent extras
        Intent intent = getIntent();
        categoryName = intent.getStringExtra("category");

        cat_name.setText(categoryName);

        //Sets up adapters for recycler view
        setupView();

        //Click listener to go back to main activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //click listener to go to settings
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, SettingsActivity.class);
                CategoryActivity.this.startActivity(intent);
            }
        });
        //click listener to go to activity to add a new recipe
        addRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAdd = new Intent(CategoryActivity.this, AddRecipeActivity.class);
                intentAdd.putExtra("category", categoryName);
                intentAdd.putExtra("mode", 0);
                CategoryActivity.this.startActivity(intentAdd);
            }
        });
    }

    //Attaches adapter to recycler view to create list of recipes
    private void setupView() {
        mRecipes = new ArrayList<>();
        //Opens internal storage
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(categoryName, null);

        //Checks if list of categories exists, if so holds the list
        if(!(json == null)){
            Type type = new TypeToken<ArrayList<RecipeModel>>() {}.getType();
            recipes = gson.fromJson(json,type);
        }
        //Creates an ArrayList of FullRecipeModels so that images can be stored
        for(int i = 0; i < recipes.size();i++){
            RecipeModel m = recipes.get(i);
            FullRecipeModel recipe = new FullRecipeModel(m.getName(), m.getTags(), m.getDirections(),
                    m.getIngredients(), m.getQuantities());
            recipe.setImg(loadImageFromStorage(m.getImgPath()));
            mRecipes.add(recipe);
        }

        //sets up recipe recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CategoryActivity.this);
        recipe_list.setLayoutManager(layoutManager);
        adapter = new RecipeListAdapter(this, mRecipes, this);
        recipe_list.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Immediately updates the view after adding a new recipe
        setupView();

        //checks if theme has changed since activity start, refreshes if it has
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        int newTheme = sharedPreferences.getInt("currentTheme", R.style.Theme_TheFamilyCookbook);
        if(newTheme != theme){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    //Allows for ingredient deletion
    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("index", position);
        intent.putExtra("category", categoryName);
        this.startActivity(intent);
    }

    /*
    Purpose: receives absolute path of image and retrieves it from internal storage and returns it
    */
    private Bitmap loadImageFromStorage(String path){
        try {
            File file = new File(path);
            Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(file));
            return bm;
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    /*
    Purpose: Handles deletion of recipe mechanic clarifying the intention of the user with an
    alert dialog before deleting recipe. Then removes it from all storages.
     */
    @Override
    public void onRecipeLongClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this, R.style.alertDialogStyle);
        builder.setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete: "+mRecipes.get(position).getName()+"?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //removes recipe's saved image from files
                        File file = new File(recipes.get(position).getImgPath());
                        file.delete();
                        //removes recipe from array list re-saves new list
                        recipes.remove(position);
                        mRecipes.remove(position);
                        saveToPref();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Does nothing
                    }
                })
                .show();
    }

    //Saves Arraylist of recipes to shared preferences for storage
    private void saveToPref() {
        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Saves arraylist of recipes to shared preferences
        String json = gson.toJson(recipes);
        editor.putString(categoryName, json);
        editor.commit();
    }
}