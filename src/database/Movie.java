package database;

import fileio.MovieInputData;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

/* Clasa Movie implementata de mine. */
public class Movie extends Show{
    private int duration;

    private ArrayList<Double> ratings;

    public Movie(String title, int year, ArrayList<String> cast, ArrayList<String> genres, int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
        this.ratings = new ArrayList<>();
    }

    public Movie(MovieInputData movie) {
        super(movie.getTitle(), movie.getYear(), movie.getCast(), movie.getGenres());
        this.duration = movie.getDuration();
        this.ratings = new ArrayList<>();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ArrayList<Double> getRatings() {
        return ratings;
    }

    public void setRatings(ArrayList<Double> ratings) {
        this.ratings = ratings;
    }

    public double calculateShowGrade() {
        if (ratings.isEmpty()) {
            return 0;
        } else {
            double sum = 0.0;
            for (Double currentRating : ratings) {
                sum += currentRating;
            }
            return sum / ratings.size();
        }
    }

    public boolean isFromYear(List<String> yearList) {
        if (yearList.isEmpty())
            return true;
        if (Integer.toString(this.getYear()).equals(yearList.get(0)))
            return true;
        return false;
    }

    public boolean hasGenres(List<String> genreList) {
        if (genreList.isEmpty())
            return true;
        for (String currentGenre : genreList) {
            if (!this.getGenres().contains(currentGenre))
                return false;
        }
        return true;
    }

    public int numberOfFavorites(Database database) {
        int numberOfFavorites = 0;
        for (User currentUser : database.getUsersMap().values()) {
            for (String currentMovie : currentUser.getFavoriteMovies()) {
                if (this.getTitle().equals(currentMovie) && database.getMoviesMap().containsKey(currentMovie))
                    /* Verific si daca videoclipul din lista de favorite este film. Posibil sa nu fie nevoie*/
                    numberOfFavorites++;
            }
        }
        return numberOfFavorites;
    }

    @Override
    public String toString() {
        return "MovieInputData{" + "title= "
                + super.getTitle() + "year= "
                + super.getYear() + "duration= "
                + duration + "cast {"
                + super.getCast() + " }\n"
                + "genres {" + super.getGenres() + " }\n ";
    }
}
