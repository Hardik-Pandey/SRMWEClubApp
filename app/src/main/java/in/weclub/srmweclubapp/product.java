package in.weclub.srmweclubapp;

public class product {
    private int id;
    private String title;
    private String shortdesc;
    private String disc;
    private int image;
    public product(int id, String title, String shortdesc,String disc, int image) {
        this.id = id;
        this.title = title;
        this.shortdesc = shortdesc;

        this.disc = disc;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getShortdesc() {
        return shortdesc;
    }
    public String getDisc() {
        return disc;
    }

    public int getImage() {
        return image;
    }
}

