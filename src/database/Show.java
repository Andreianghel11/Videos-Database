package database;

import entertainment.Genre;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Clasa definește obiectele de tip show.
 * Este extinsă de clase Movie și Serial.
 */
public abstract class Show {
    private String title;

    private int year;

    private int id;

    private int nrOfFavorites;

    private int nrOfViews;

    private ArrayList<String> cast;

    private ArrayList<String> genres;

    /**
     * Constructor standard.
     */
    public Show(final String title, final int year, final ArrayList<String> cast,
                final ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(final String title) {
        this.title = title;
    }

    public final int getYear() {
        return year;
    }

    public final void setYear(final int year) {
        this.year = year;
    }

    public final int getId() {
        return id;
    }

    public final void setId(final int id) {
        this.id = id;
    }

    public final int getNrOfFavorites() {
        return nrOfFavorites;
    }

    public final void setNrOfFavorites(final int nrOfFavorites) {
        this.nrOfFavorites = nrOfFavorites;
    }

    public final int getNrOfViews() {
        return nrOfViews;
    }

    public final void setNrOfViews(final int nrOfViews) {
        this.nrOfViews = nrOfViews;
    }

    public final ArrayList<String> getCast() {
        return cast;
    }

    public final void setCast(final ArrayList<String> cast) {
        this.cast = cast;
    }

    public final ArrayList<String> getGenres() {
        return genres;
    }

    public final void setGenres(final ArrayList<String> genres) {
        this.genres = genres;
    }

    /**
     * Metodă de calculare a rating-ului unui show.
     * Este implementată de clasele Movie și Serial.
     */
    public abstract double calculateShowGrade();

    /**
     * Verifică dacă show-ul curent este din anul primit ca parametru.
     */
    public final boolean isFromYear(final List<String> yearList) {
        if (yearList.get(0) == null) {
            return true;
        }
        if (Integer.toString(this.getYear()).equals(yearList.get(0))) {
            return true;
        }
        return false;
    }

    /**
     * Verifică dacă show-ul curent conține toate
     * genurile din lista primită ca parametru.
     */
    public final boolean hasAllGenres(final List<String> genreList) {
        if (genreList.get(0) == null) {
            return true;
        }
        for (String currentGenre : genreList) {
            if (!this.getGenres().contains(currentGenre)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifică dacă show-ul curent conține genul primit ca parametru.
     */
    public final boolean hasGenre(final Genre genre) {
        for (String currentGenre : this.getGenres()) {
            if (Utils.stringToGenre(currentGenre).equals(genre)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Stabilește de câte ori a fost adăugat show-ul curent
     * în listele de favorite ale utilizatorilor. Sunt parcurse
     * toate listele de favorite ale utilizatorilor din baza de date.
     */
    public final void numberOfFavorites(final Database database) {
        int numberOfFavorites = 0;
        for (User currentUser : database.getUsersMap().values()) {
            for (String currentShow : currentUser.getFavoriteMovies()) {
                if (this.getTitle().equals(currentShow)) {
                    numberOfFavorites++;
                }
            }
        }
        this.setNrOfFavorites(numberOfFavorites);
    }

    /**
     * Stabilește numărul de vizionări ale show-urilor. Sunt
     * parcurse toate istoricele utilizatorilor din baza de date.
     */
    public final void numberOfViews(final Database database) {
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
