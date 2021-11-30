package database;

import java.util.ArrayList;
import java.util.List;

public abstract class Show {
    private String title;

    private int year;

    private ArrayList<String> cast;

    private ArrayList<String> genres;

    public Show(String title, int year, ArrayList<String> cast, ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArrayList<String> getCast() {
        return cast;
    }

    public void setCast(ArrayList<String> cast) {
        this.cast = cast;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public abstract double calculateShowGrade();

    public abstract boolean isFromYear(List<String> yearList);

    public abstract boolean hasGenres(List<String> genreList);

    public abstract int numberOfFavorites(Database database);

    public abstract int numberOfViews(Database database);
}
