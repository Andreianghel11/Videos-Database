package database;

import actor.ActorsAwards;
import fileio.ActorInputData;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/*Clasa Actor facuta de mine.*/
public class Actor {

    private String name;

    private String careerDescription;

    private ArrayList<String> filmography;

    /*Nume Award, numar.*/
    private Map<ActorsAwards, Integer> awards;

    public Actor(String name, String careerDescription, ArrayList<String> filmography, Map<ActorsAwards, Integer> awards) {
        this.name = name;
        this.careerDescription = careerDescription;
        this.filmography = filmography;
        this.awards = awards;
    }

    /* Constructor special. */
    public Actor(ActorInputData actor) {
        this.name = actor.getName();
        this.careerDescription = actor.getCareerDescription();
        this.filmography = actor.getFilmography();
        this.awards = actor.getAwards();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public void setCareerDescription(String careerDescription) {
        this.careerDescription = careerDescription;
    }

    public ArrayList<String> getFilmography() {
        return filmography;
    }

    public void setFilmography(ArrayList<String> filmography) {
        this.filmography = filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public void setAwards(Map<ActorsAwards, Integer> awards) {
        this.awards = awards;
    }

    public double calculateActorRating(Database database) {
        double rating = 0.0;
        int numberOfRatings = 0;
        for (String name : filmography) {
            Movie currentMovie = database.getMoviesMap().get(name);
            Serial currentSerial = database.getSerialsMap().get(name);
            if (currentMovie != null) {
                if (currentMovie.calculateMovieGrade() != 0)
                    numberOfRatings++;
                rating += currentMovie.calculateMovieGrade();
            } else if (currentSerial != null){
                if (currentSerial.calculateSerialGrade() != 0)
                    numberOfRatings++;
                rating += currentSerial.calculateSerialGrade();
            }
        }
        return rating / numberOfRatings;
    }

    public int calculateNumberOfAwards() {
        int sum = 0;
        for (Integer number : awards.values()) {
            sum += number;
        }
        return sum;
    }

    public boolean descriptionContainsWord(String word) {
        return this.careerDescription.toLowerCase().contains(word.toLowerCase());
    }

    @Override
    public String toString() {
        return "ActorInputData{"
                + "name='" + name + '\''
                + ", careerDescription='"
                + careerDescription + '\''
                + ", filmography=" + filmography + '}';
    }
}
