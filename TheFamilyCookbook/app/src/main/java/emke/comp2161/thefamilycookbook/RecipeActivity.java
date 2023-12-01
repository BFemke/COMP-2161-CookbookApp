package emke.comp2161.thefamilycookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import emke.comp2161.thefamilycookbook.adapters.DirectionAdapter;
import emke.comp2161.thefamilycookbook.adapters.IngredientAdapter;
import emke.comp2161.thefamilycookbook.models.RecipeModel;

public class RecipeActivity extends AppCompatActivity implements DirectionAdapter.OnDirectionListener,
        IngredientAdapter.OnIngredientListener{

    private ImageView imageView;
    private TextView recipeNameTV;
    private TextView tagTV;
    private RecyclerView ingredientList;
    private RecyclerView quantityList;
    private RecyclerView directionList;
    private ImageButton backBtn;
    private Button editBtn;
    private String recipeName;
    private int recipeNum;
    private Gson gson = new Gson();
    private String recipeCategory;
    private ArrayList<RecipeModel> recipes = new ArrayList<>();

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets theme of activity as the stored theme
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("currentTheme", R.style.Theme_TheFamilyCookbook);
        setTheme(theme);
        setContentView(R.layout.activity_recipe);

        //Gets extra content from intent
        Intent intent = getIntent();
        recipeCategory = intent.getStringExtra("category");
        recipeNum = intent.getIntExtra("index", 0);

        //calls function to get list of recipes from shared preferences
        getRecipes(recipeCategory);

        recipeNameTV = findViewById(R.id.settings_heading);
        recipeNameTV.setText(recipeName);

        //Sets of view with content of the selected recipe
        setUpView();

        //Sets button listeners for every button in activity
        setBtnListeners();

    }

    //Sets listeners for buttons in activity
    private void setBtnListeners() {
        backBtn = findViewById(R.id.setting_back_btn);
        editBtn = findViewById(R.id.edit_btn);

        //Brings user to edit recipe window
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentEdit = new Intent(RecipeActivity.this, AddRecipeActivity.class);
                intentEdit.putExtra("mode", 1);
                intentEdit.putExtra("category", recipeCategory);
                intentEdit.putExtra("index", recipeNum);
                RecipeActivity.this.startActivity(intentEdit);
            }
        });

        //Returns user back to the categories activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //Used to make sure information is updated immediately if user edits recipe
    @Override
    protected void onResume() {
        super.onResume();
        getRecipes(recipeCategory);
        recipeNameTV.setText(recipeName);

        setUpView();
    }

    /*
        I use the index number of the desired recipe incase there are two recipes of the same name,
        this way we reference the correct one.
         */
    private void setUpView() {
        tagTV = findViewById(R.id.tag_list_tv);
        ingredientList = findViewById(R.id.selected_ingredient_list);
        quantityList = findViewById(R.id.selected_ingredient_quantity_list);
        directionList = findViewById(R.id.selected_direction_list);
        imageView = findViewById(R.id.recipe_img_view);

        ArrayList<String> tagAL = arrayToArrayList(recipes.get(recipeNum).getTags());
        ArrayList<String> ingredientAL = arrayToArrayList(recipes.get(recipeNum).getIngredients());
        ArrayList<String> quantityAL = arrayToArrayList(recipes.get(recipeNum).getQuantities());
        ArrayList<String> directionAL = arrayToArrayList(recipes.get(recipeNum).getDirections());

        //Sets image of recipe
        imageView.setImageBitmap(loadImageFromStorage(recipes.get(recipeNum).getImgPath()));

        //Sets up tag TextView in a comma separated list
        String tags = "";
        for(int i = 0; i < tagAL.size(); i++){
            tags += tagAL.get(i);
            if((tagAL.size()-i) > 1){
                tags += ", ";
            }
        }
        tagTV.setText(tags);

        //Sets adapter for ingredient list view
        ingredientList.setLayoutManager(new LinearLayoutManager(this));
        quantityList.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapter adapterI = new IngredientAdapter(this, ingredientAL, this);
        ingredientList.setAdapter(adapterI);
        IngredientAdapter adapterQ = new IngredientAdapter(this, quantityAL, this);
        quantityList.setAdapter(adapterQ);

        //Sets adapter for direction list view
        directionList.setLayoutManager(new LinearLayoutManager(this));
        DirectionAdapter adapterD = new DirectionAdapter(this, directionAL, this);
        directionList.setAdapter(adapterD);
    }

    /*
    Purpose: Loads image bitmap from internal storage using absolute filepath
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
    Creates an Array List from a string array of values
     */
    private ArrayList<String> arrayToArrayList(String[] array) {
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i =0; i < array.length; i++){
            arrayList.add(array[i]);
        }
        return arrayList;
    }

    /*
    Gets the list of recipes from the category from internal storage.
     */
    private void getRecipes(String recipeCategory) {
        //Opens internal storage
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(recipeCategory, null);

        //Checks if list of categories exists, if so holds the list
        if(!(json == null)){
            Type type = new TypeToken<ArrayList<RecipeModel>>() {}.getType();
            recipes = gson.fromJson(json,type);
        }

        recipeName = recipes.get(recipeNum).getName();
    }

    //Mandatory empty override, we don't need this function in this activity
    @Override
    public void onDirectionClick(int position){
    }

    //Mandatory empty override, we don't need this function in this activity
    @Override
    public void onIngredientClick(int position) {

    }
}