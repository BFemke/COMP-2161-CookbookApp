package emke.comp2161.thefamilycookbook.models;

import android.graphics.Bitmap;

public class CategoryModel {
    String name;

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    String imgPath;
    Bitmap img;

    public  CategoryModel() {
        //Mandatory empty constructor
    }

    public CategoryModel(String name, Bitmap img) {
        this.name = name;
        this.img = img;
    }

    public CategoryModel(String name, String imgPath){
        this.name = name;
        this.imgPath = imgPath;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

}
