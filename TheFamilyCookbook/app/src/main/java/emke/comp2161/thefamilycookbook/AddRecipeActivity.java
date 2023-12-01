package emke.comp2161.thefamilycookbook;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import emke.comp2161.thefamilycookbook.adapters.DirectionAdapter;
import emke.comp2161.thefamilycookbook.adapters.IngredientAdapter;
import emke.comp2161.thefamilycookbook.adapters.TagAdapter;
import emke.comp2161.thefamilycookbook.models.RecipeModel;

public class AddRecipeActivity extends AppCompatActivity implements DirectionAdapter.OnDirectionListener,
        TagAdapter.OnTagListener, IngredientAdapter.OnIngredientListener {

    private Spinner categorySpinner;
    private EditText recipeName;
    private ImageView recipeImg;
    private ImageButton addImg;
    private RecyclerView tagList;
    private EditText tagEditText;
    private ImageButton addTag;
    private RecyclerView ingredientList;
    private EditText ingredientEditText;
    private RecyclerView quantityList;
    private EditText quantityEditText;
    private ImageButton addIngredient;
    private RecyclerView directionList;
    private EditText directionEditText;
    private ImageButton addDirection;
    private Button saveRecipe;
    private ImageButton backBtn;
    private ArrayList<String> tags = new ArrayList<>();
    private ArrayList<String> ingredients = new ArrayList<>();
    private ArrayList<String> directions = new ArrayList<>();
    private ArrayList<String> quantities = new ArrayList<>();
    private TagAdapter adapterT;
    private IngredientAdapter adapterI;
    private DirectionAdapter adapterD;
    private IngredientAdapter adapterQ;
    private String[] categoryList;
    private Uri imageUri;
    private int index;
    private int mode;
    int theme;
    private ArrayList<RecipeModel> recipes = new ArrayList<>();

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets theme of activity as the stored theme
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("currentTheme", R.style.Theme_TheFamilyCookbook);
        setTheme(theme);
        setContentView(R.layout.activity_add_recipe);

        //Initializes elements from the  layout
        backBtn = findViewById(R.id.setting_back_btn);
        categorySpinner = findViewById(R.id.category_spinner);
        recipeName = findViewById(R.id.recipe_name);
        recipeImg = findViewById(R.id.recipe_img_view);
        addImg = findViewById(R.id.add_img_btn);
        tagList = findViewById(R.id.tag_list_tv);
        tagEditText = findViewById(R.id.tag);
        addTag = findViewById(R.id.add_tag);
        ingredientList = findViewById(R.id.selected_ingredient_list);
        ingredientEditText = findViewById(R.id.ingredient);
        quantityList = findViewById(R.id.selected_ingredient_quantity_list);
        quantityEditText = findViewById(R.id.quantity);
        addIngredient = findViewById(R.id.add_ingredient_btn);
        directionList = findViewById(R.id.selected_direction_list);
        directionEditText = findViewById(R.id.direction);
        addDirection = findViewById(R.id.add_direction_btn);
        saveRecipe = findViewById(R.id.edit_btn);
        TextView modeTitle = findViewById(R.id.settings_heading);

        //Gather any intent extras
        Intent intent = getIntent();
        String categoryName = intent.getStringExtra("category");
        mode = intent.getIntExtra("mode", 0);

        //checks if user entered activity to add new recipe
        if(mode == 0){
            modeTitle.setText("Add New Recipe");
        }
        //edits origin recipe
        else{
            modeTitle.setText("Edit Recipe");
            index = intent.getIntExtra("index", 0);
            getInitialContent(index, categoryName);
        }

        //Sets up form with adapters and initializes content of elements
        setupForm(categoryName);

        //Adds listeners for all buttons in the activity
        addListeners();

    }

    //Used to make sure any updates in theme selection are immediately reflected
    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        int newTheme = sharedPreferences.getInt("currentTheme", R.style.Theme_TheFamilyCookbook);
        //checks if theme has changed since activity start, refreshes if it has
        if(newTheme != theme) {
            setTheme(newTheme);
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    //Adds listeners to all buttons in the activity
    private void addListeners() {
        //Takes care of adding tags to an arraylist and updates listview
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tagEditText.getText().toString().equals("")){
                    Toast.makeText(AddRecipeActivity.this, "Please enter a tag..", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Allows for maximum of 3 tags
                    if(tags.size() <= 3) {
                        tags.add(tagEditText.getText().toString());
                        tagEditText.setText("");
                        if (tags.size() == 3) {
                            Toast.makeText(AddRecipeActivity.this, "You have entered the max amount of tags", Toast.LENGTH_SHORT).show();
                        }
                        adapterT.notifyDataSetChanged();
                    }
                    else{
                        Toast.makeText(AddRecipeActivity.this, "You must delete a tag before entering a new one", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Takes care of adding ingredient to an arraylist and updates listview
        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ingredientEditText.getText().toString().equals("")){
                    Toast.makeText(AddRecipeActivity.this, "Please enter an ingredient..", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(quantityEditText.getText().toString().equals("")){
                        quantities.add(" ");
                    }
                    else{
                        quantities.add(quantityEditText.getText().toString());
                        quantityEditText.setText("");
                    }
                    ingredients.add(ingredientEditText.getText().toString());
                    ingredientEditText.setText("");
                    adapterI.notifyDataSetChanged();
                    adapterQ.notifyDataSetChanged();
                }
            }
        });

        //Takes care of adding directions to an arraylist and updates listview
        addDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(directionEditText.getText().toString().equals("")){
                    Toast.makeText(AddRecipeActivity.this, "Please enter a direction..", Toast.LENGTH_SHORT).show();
                }
                else{
                    directions.add(directionEditText.getText().toString());
                    directionEditText.setText("");
                    adapterD.notifyDataSetChanged();
                }
            }
        });

        //Brings user to activity to select image from gallery
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                resultLauncher.launch(Intent.createChooser(intent, "Select Image"));
            }
        });

        //Goes back to previous activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //saves recipe if valid form and goes back to previous activity
        saveRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValidForm()){
                    saveRecipe();
                    finish();
                }
            }
        });
    }

    //Handles selection of image from phone gallery
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts
                    .StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                recipeImg.setImageURI(imageUri);

            }
        }
    });

    /*
    Purpose: Gather all information from the inputs in the form, gets current- list of recipes in
    designated category if it exists, if not then it creates a new list and adds the new recipe
    saving it to its category specific shared pref when done
     */
    private void saveRecipe() {
        Gson gson = new Gson();
        String category = categorySpinner.getSelectedItem().toString();

        //Opens internal storage
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(category, null);

        //Gets list of current recipes in category if it exists
        if(!(json == null)){
            Type type = new TypeToken<ArrayList<RecipeModel>>() {}.getType();
            recipes = gson.fromJson(json, type);
        }

        //Gets name of recipe
        String name = recipeName.getText().toString();

        //Gets lists of other values
        String[] tagArray = arrayListToArray(tags);
        String[] ingredientArray = arrayListToArray(ingredients);
        String[] quantityArray = arrayListToArray(quantities);
        String[] directionArray = arrayListToArray(directions);

        //creates new recipe
        RecipeModel recipe = new RecipeModel(name, tagArray, directionArray,
                ingredientArray, quantityArray);


        //Gets image, if none selected sets default
        Bitmap bm = null;
        if(imageUri == null){
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_recipe_img);
        }
        else{
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //adds new recipe if not editing
        if(mode == 0){
            recipes.add(recipe);
            saveImageToStorage(bm, recipes.size()-1);
        }
        //Rewrites previous recipe if editing
        else{
            recipes.set(index, recipe);
            saveImageToStorage(bm, index);
        }

        saveToPref(category);
    }

    /*
    Purpose: Saves list of recipes for a specific category in shared preferences
     */
    private void saveToPref(String category){
        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Saves arraylist of recipes to shared preferences
        String json = gson.toJson(recipes);
        editor.putString(category, json);
        editor.commit();
    }

    /*
    Purpose: Saves image of category to internal storage and associates path with category
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void saveImageToStorage(Bitmap bm, int index) {
        String filename = System.currentTimeMillis()+".jpg";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(categorySpinner.getSelectedItem().toString(), Context.MODE_PRIVATE);

        //creates new file
        File file=new File(directory,filename);

        //Writes image to file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Associates stored image with recipe
        recipes.get(index).setImgPath(file.getAbsolutePath());
    }

    /*
    Purpose: receives absolute path of image and retrieves it from internal storage and returns it
     */
    public Bitmap loadImageFromStorage(String path){
        try {
            File file = new File(path);
            Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(file));
            return bm;
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        //returns null if it fails
        return null;
    }

    //Gets string array from String arraylist
    private String[] arrayListToArray(ArrayList<String> arrayList) {
        if(arrayList.size() == 0){
            return new String[0];
        }
        String[] array = new String[arrayList.size()];
        for(int i = 0; i < arrayList.size();i++) {
            array[i] = arrayList.get(i);
        }
       return array;
    }

    //Gets string array list from string array
    private ArrayList<String> arrayToArrayList(String[] array){
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i = 0; i < array.length;i++){
            arrayList.add(array[i]);
        }
        return arrayList;
    }

    /*
    Purpose: Checks if the recipe form has the two mandatory fields
     */
    private boolean checkValidForm() {
        if(categorySpinner.getSelectedItem().toString().equals("Please select a category..")){
            Toast.makeText(this, "Select a category", Toast.LENGTH_SHORT).show();
            return  false;
        }
        else if(recipeName.getText().toString().equals("")){
            Toast.makeText(this, "Enter a name for the recipe", Toast.LENGTH_SHORT).show();
            return  false;
        }
        return true;
    }

    /*
    Purpose: Creates a pre-filled form populated with the information of the recipe the user wants
    to edit
     */
    private void getInitialContent(int index, String categoryName) {
        ArrayList<RecipeModel> recipes = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(categoryName, null);
        Gson gson = new Gson();
        if(!(json == null)){
            Type type = new TypeToken<ArrayList<RecipeModel>>() {}.getType();
            recipes = gson.fromJson(json,type);
        }
        RecipeModel recipe = recipes.get(index);

        //Sets all initial values with what was already in the recipe
        tags = arrayToArrayList(recipe.getTags());
        ingredients = arrayToArrayList(recipe.getIngredients());
        quantities = arrayToArrayList(recipe.getQuantities());
        directions = arrayToArrayList(recipe.getDirections());
        recipeImg.setImageBitmap(loadImageFromStorage(recipe.getImgPath()));
        recipeName.setText(recipe.getName());

    }

    /*
    Purpose: Sets adapters for the recycler views and sets up spinner for selection
     */
    private void setupForm(String categoryName) {

        /*
        This section sets of the list view adapters
         */
        //Sets adapter for tag list view
        tagList.setLayoutManager(new LinearLayoutManager(this));
        adapterT = new TagAdapter(this, tags, this);
        tagList.setAdapter(adapterT);

        //Sets adapter for ingredient list view
        ingredientList.setLayoutManager(new LinearLayoutManager(this));
        quantityList.setLayoutManager(new LinearLayoutManager(this));
        adapterI = new IngredientAdapter(this, ingredients, this);
        ingredientList.setAdapter(adapterI);
        adapterQ = new IngredientAdapter(this, quantities, this);
        quantityList.setAdapter(adapterQ);

        //Sets adapter for direction list view
        directionList.setLayoutManager(new LinearLayoutManager(this));
        adapterD = new DirectionAdapter(this, directions, this);
        directionList.setAdapter(adapterD);

        /*
        This section Sets up the category spinner by getting all the category names from
        shared preferences
         */
        SharedPreferences sharedPreferences;
        Gson gson = new Gson();
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("names", null);
        int index = 0;
        if(!(json == null)){
            ArrayList<String> categories;
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            categories = gson.fromJson(json,type);
            categoryList = new String[categories.size()];
            for(int i = 0; i < categories.size();i++){
                categoryList[i] = categories.get(i);
                if(categoryName.equals(categories.get(i))){
                    index = i;
                }
            }
        }

        //Sets up spinner to show category names array for selection
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddRecipeActivity.this,
                android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setSelection(index);
    }

    /*
    Purpose: Confirms if the user wants to delete the specified item
     */
    private void confirmDelete(int type, int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddRecipeActivity.this, R.style.alertDialogStyle);
        builder.setTitle("Delete")
                .setMessage("Are you sure  you want to delete this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //if a tag is selected to be deleted
                        if(type == 1){
                            tags.remove(index);
                            adapterT.notifyDataSetChanged();

                        }
                        //if an ingredient w/ quantity is selected to be deleted
                        else if(type == 2){
                            ingredients.remove(index);
                            adapterI.notifyDataSetChanged();
                            quantities.remove(index);
                            adapterQ.notifyDataSetChanged();
                        }
                        //if a direction is selected to be deleted
                        else{
                            directions.remove(index);
                            adapterD.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    //Allows for direction deletion
    @Override
    public void onDirectionClick(int position) {
        confirmDelete(3, position);
    }

    //Allows for tag deletion
    @Override
    public void onTagClick(int position) {
        confirmDelete(1, position);
    }

    //Allows for ingredient deletion
    @Override
    public void onIngredientClick(int position) {
        confirmDelete(2, position);
    }
}