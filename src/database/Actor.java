package database;

import actor.ActorsAwards;
import fileio.ActorInputData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Clasa definește obiectele de tip actor.
 */
public final class Actor {

    private String name;

    private String careerDescription;

    private ArrayList<String> filmography;

    private double actorRating;

    private Map<ActorsAwards, Integer> awards;

    /**
     * Constructor specializat.
     */
    public Actor(final ActorInputData actor) {
        this.name = actor.getName();
        this.careerDescription = actor.getCareerDescription();
        this.filmography = actor.getFilmography();
        this.awards = actor.getAwards();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public void setCareerDescription(final String careerDescription) {
        this.careerDescription = careerDescription;
    }

    public ArrayList<String> getFilmography() {
        return filmography;
    }

    public void setFilmography(final ArrayList<String> filmography) {
        this.filmography = filmography;
    }

    public double getActorRating() {
        return actorRating;
    }

    public void setActorRating(final double actorRating) {
        this.actorRating = actorRating;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public void setAwards(final Map<ActorsAwards, Integer> awards) {
        this.awards = awards;
    }

    /**
     * Calculează rating-ul actorului curent.
     * Primește ca parametru baza de date pentru a
     * putea accesa show-urile și rating-urile lor.
     */
    public void calculateActorRating(final Database database) {
        double rating = 0.0;
        int numberOfRatings = 0;
        for (String currentName : filmography) {
            Movie currentMovie = database.getMoviesMap().get(currentName);
            Serial currentSerial = database.getSerialsMap().get(currentName);
            if (currentMovie != null) {
                if (currentMovie.calculateShowGrade() > 0) {
                    numberOfRatings++;
                }
                rating += currentMovie.calculateShowGrade();
            } else if (currentSerial != null) {
                if (currentSerial.calculateShowGrade() > 0) {
                    numberOfRatings++;
                }
                rating += currentSerial.calculateShowGrade();
            }
        }
        if (numberOfRatings != 0) {
            this.setActorRating(rating / numberOfRatings);
        } else {
            this.setActorRating(0);
        }
    }

    /**
     * Calculează numărul de premii deținute de un actor.
     */
    public int calculateNumberOfAwards() {
        int sum = 0;
        for (Integer number : awards.values()) {
            sum += number;
        }
        return sum;
    }

    /**
     * Caută în descrierea actorului String-ul primit ca parametru.
     */
    public boolean descriptionContainsWord(final String word) {
        List<String> stringList;
        stringList = Arrays
                .stream(this.careerDescription.toLowerCase().split("\\W+")).toList();
        for (String currentString : stringList) {
            if (currentString.equals(word)) {
                return true;
            }
        }
        return false;
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
