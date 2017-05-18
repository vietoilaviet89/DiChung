package model;

/**
 * Created by 123456789 on 4/25/2017.
 */

public class InformationItem {
    private String title;
    private String info;

    public InformationItem(){

    }

    public InformationItem(String title, String info) {
        this.title = title;
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
