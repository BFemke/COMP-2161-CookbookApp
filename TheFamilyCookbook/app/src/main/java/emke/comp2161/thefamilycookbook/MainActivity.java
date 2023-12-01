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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import emke.comp2161.thefamilycookbook.adapters.CategoryAdapter;
import emke.comp2161.thefamilycookbook.models.CategoryModel;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryListener {

    private ArrayList<CategoryModel> categories = new ArrayList<>();
    private ImageView addCatImg;
    private RecyclerView cat_list;
    private ImageButton add_cat;
    private CategoryAdapter adapter;
    private Gson gson = new Gson();
    private Uri imageUri;
    private ArrayList<String> nameArray = new ArrayList<>();
    private ArrayList<String> pathArray = new ArrayList<>();
    private int theme;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets theme of activity as the stored theme
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("currentTheme", R.style.Theme_TheFamilyCookbook);
        setTheme(theme);
        setContentView(R.layout.activity_main);

        cat_list = findViewById(R.id.cat_recycler);
        add_cat = findViewById(R.id.add_category);
        ImageButton toSettingsBtn = findViewById(R.id.recipeSettingsBtn);

        //Gets values out of shared preferences
        String json1 = sharedPreferences.getString("names", null);
        String json2 = sharedPreferences.getString("paths", null);
        //if not category is stored it calls to initialize default categories
        if(json1 == null || json2 == null){
            initializeCategories();
        }
        //if categories had been stored it calls function to create the recycler view
        else {
            setUpCategories(json1, json2);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        cat_list.setLayoutManager(layoutManager);
        adapter = new CategoryAdapter(this, categories, this);
        cat_list.setAdapter(adapter);

        //Sets up listener in order to add custom category
        add_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory();
            }
        });

        //click listener to go to settings
        toSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    //Used to make sure any updates in theme selection are immediately reflected
    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        int newTheme = sharedPreferences.getInt("currentTheme", R.style.Theme_TheFamilyCookbook);
        //checks if theme has changed since activity start, refreshes if it has
        if(newTheme != theme){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    /*
        Purpose: Sets up the category information for the recycler view if it category information
        has been stored in shared preferences
     */
    private void setUpCategories(String json1, String json2) {
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        nameArray = gson.fromJson(json1, type);
        pathArray = gson.fromJson(json2, type);
        //adds stored values for categories from shared pref and gets saved image
        for(int i = 0; i < nameArray.size();i++){
            categories.add(new CategoryModel(nameArray.get(i), pathArray.get(i)));
            loadImageFromStorage(pathArray.get(i), i);
        }
    }

    private void addCategory() {

        //Creates alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.alertDialogStyle);
        View view = getLayoutInflater().inflate(R.layout.add_category_dialog, null);
        EditText addCatName = view.findViewById(R.id.addCatName);
        addCatImg = view.findViewById(R.id.addCatImg);
        Drawable drawable = addCatImg.getDrawable();
        ImageButton getImgBtn = view.findViewById(R.id.getImgBtn);

        //Sets dialog to be shown
        AlertDialog show = builder.setView(view)
                .setTitle("Add a Category")
                //Saves new category name and img in category object
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(addCatName.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this, "Please enter a name for the category..",
                                    Toast.LENGTH_SHORT).show();
                            addCategory();
                        }
                        else{
                            //Get values for new category
                            String catName = addCatName.getText().toString();
                            Bitmap bm = null;
                            //sets image as default image if non selected
                            if(imageUri == null){
                                bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_cat_back);
                            }
                            //else stores user's selected image
                            else {
                                try {
                                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                } catch (IOException e) {
                                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                            //Adds and saves new category
                            categories.add(new CategoryModel(catName, bm));
                            nameArray.add(catName);
                            saveImageToStorage(bm, categories.size() - 1);
                            adapter.notifyDataSetChanged();
                            saveToPref();
                        }
                    }
                })
                //Exits dialog with no changes made
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();

        //Brings you to gallery to pick a photo
        getImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                resultLauncher.launch(Intent.createChooser(intent, "Select Image"));
            }
        });
    }

    /*
    Purpose: Saves image of category to internal storage and associates path with category
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void saveImageToStorage(Bitmap bm, int index) {
        String filename = System.currentTimeMillis()+".jpg";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("Categories", Context.MODE_PRIVATE);

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

            //Associates stored image with category
            categories.get(index).setImgPath(file.getAbsolutePath());
            pathArray.add(file.getAbsolutePath());

    }

    /*
    Purpose: receives absolute path of image and retrieves it from internal storage and returns it
    */
    private void loadImageFromStorage(String path, int index){
        try {
            File file = new File(path);
            Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(file));
            categories.get(index).setImg(bm);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    //Allows selecting an image from phone gallery
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts
            .StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                addCatImg.setImageURI(imageUri);

            }
        }
    });

    /*
    Purpose: Creates new default category items, upon first use of the app
     */
    private void initializeCategories() {
        //Creates arrays of stored default data
        nameArray = ArrayToArrayList(getResources().getStringArray(R.array.category_names));
        Bitmap appetizer = BitmapFactory.decodeResource(getResources(), R.drawable.appetizers);
        Bitmap entree = BitmapFactory.decodeResource(getResources(), R.drawable.entree);
        Bitmap soup = BitmapFactory.decodeResource(getResources(), R.drawable.soup);
        Bitmap salad = BitmapFactory.decodeResource(getResources(), R.drawable.salad);
        Bitmap dessert = BitmapFactory.decodeResource(getResources(), R.drawable.dessert);
        Bitmap snack = BitmapFactory.decodeResource(getResources(), R.drawable.snacks);
        Bitmap preserves = BitmapFactory.decodeResource(getResources(), R.drawable.preserves);
        Bitmap[] cat_imgs = new Bitmap[]{appetizer, entree, soup, salad, dessert, snack, preserves};

        //Creates Category objects using arrays just made and saves images in storage
        for(int i = 0; i < nameArray.size();i++){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                categories.add(new CategoryModel(nameArray.get(i), cat_imgs[i]));
                saveImageToStorage(cat_imgs[i], i);
            }
            else{
            }
        }

        saveToPref();
    }

    /*
    Purpose: Saves arraylist of the names of the categories along with the file path to their
    images in shared preferences
     */
    public void saveToPref(){
        sharedPreferences = getSharedPreferences("categories", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json1 = gson.toJson(nameArray);
        String json2 = gson.toJson(pathArray);
        editor.putString("names", json1);
        editor.putString("paths", json2);
        editor.commit();
    }

    /*
    Purpose: Gets string array and returns it converted into an Arraylist
     */
    public ArrayList<String> ArrayToArrayList(String[] array){
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i = 0; i < array.length;i++){
            arrayList.add(array[i]);
        }
        return arrayList;
    }

    /*
    Purpose: Handles click reactions, opening a new category activity, passes an intent extra of
    the category that was selected
     */
    @Override
    public void onCategoryClick(int position) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("category", categories.get(position).getName());
        this.startActivity(intent);
    }

    /*
    Purpose: handles long click of category which is deletion of category. Prompts user if they are
    sure before removing category from storage
     */
    @Override
    public void onCategoryLongClick(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.alertDialogStyle);
        builder.setTitle("Delete Category")
                .setMessage("Are you sure you want to delete: "+nameArray.get(position)+"?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Removes stored image relating to category from storage
                        File file = new File(pathArray.get(position));
                        file.delete();

                        //removes array information of category that gets store in shared preferences
                        nameArray.remove(position);
                        pathArray.remove(position);
                        categories.remove(position);
                        //updates shared preferences and vieww
                        saveToPref();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }
}