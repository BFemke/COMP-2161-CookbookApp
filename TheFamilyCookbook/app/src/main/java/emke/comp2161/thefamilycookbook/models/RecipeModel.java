package emke.comp2161.thefamilycookbook.models;

public class RecipeModel {
    String imgPath;
    String name;
    String[] tags;
    String[] directions;
    String[] ingredients;
    String[] quantities;

    public RecipeModel(String name, String[] tags, String[] directions,
                       String[] ingredients, String[] quantities) {
        this.name = name;
        this.tags = tags;
        this.directions = directions;
        this.ingredients = ingredients;
        this.quantities = quantities;
    }

    public String[] getDirections() {
        return directions;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String[] getQuantities(){return quantities;}

    public String getImgPath() {
        return imgPath;
    }

    public String getName() {
        return name;
    }

    public String[] getTags() {
        return tags;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
