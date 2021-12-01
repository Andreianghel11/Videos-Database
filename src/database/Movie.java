package database;

import entertainment.Genre;
import fileio.MovieInputData;
import utils.Utils;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

/* Clasa Movie implementata de mine. */
public class Movie extends Show{
    private int duration;

    private ArrayList<Double> ratings;

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

    @Override
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
