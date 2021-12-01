package database;

import entertainment.Genre;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class Show {
    private String title;

    private int year;

    private int id;

    private int nrOfFavorites;

    private int nrOfViews;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNrOfFavorites() {
        return nrOfFavorites;
    }

    public void setNrOfFavorites(int nrOfFavorites) {
        this.nrOfFavorites = nrOfFavorites;
    }

    public int getNrOfViews() {
        return nrOfViews;
    }

    public void setNrOfViews(int nrOfViews) {
        this.nrOfViews = nrOfViews;
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

    public boolean isFromYear(List<String> yearList) {
        if (yearList.get(0) == null)
            return true;
        if (Integer.toString(this.getYear()).equals(yearList.get(0)))
            return true;
        return false;
    }

    public boolean hasAllGenres(List<String> genreList) {
        if (genreList.get(0) == null)
            return true;
        for (String currentGenre : genreList) {
            if (!this.getGenres().contains(currentGenre))
                return false;
        }
        return true;
    }

    public boolean hasGenre(Genre genre) {
        for (String currentGenre : this.getGenres()) {
            if (Utils.stringToGenre(currentGenre).equals(genre))
                return true;
        }
        return false;
    }

    public void numberOfFavorites(Database database) {
        int numberOfFavorites = 0;
        for (User currentUser : database.getUsersMap().values()) {
            for (String currentShow : currentUser.getFavoriteMovies()) {
                if (this.getTitle().equals(currentShow))
                    /* Verific si daca videoclipul din lista de favorite este serial. Posibil sa nu fie nevoie
                     * database.getSerialsMap().containsKey(currentSerial)*/
                    numberOfFavorites++;
            }
        }
        this.setNrOfFavorites(numberOfFavorites);
    }

    public void numberOfViews(Database database) {
        int numberOfViews = 0;
        for (User currentUser : database.getUsersMap().values()) {
            for (String currentShow : currentUser.getHistory().keySet()) {
                if (this.getTitle().equals(currentShow)) {
                    numberOfViews += currentUser.getHistory().get(currentShow);
                }
            }
        }
        this.setNrOfViews(numberOfViews);
    }
}
