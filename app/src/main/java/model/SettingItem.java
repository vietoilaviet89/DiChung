package model;

/**
 * Created by 123456789 on 4/20/2017.
 */

public class SettingItem {
    private String itemName;
    private String itemImage;

    public SettingItem(){

    }

    public SettingItem(String itemName, String itemImage) {
        this.itemName = itemName;
        this.itemImage = itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }
}
