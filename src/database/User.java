package database;

import fileio.UserInputData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* Clasa User facuta de mine. */
public class User {
    private String username;

    private String subscriptionType;

    /* Nume + nota. */
    private Map<String, Integer> history;

    private ArrayList<String> favoriteMovies;

    /* Pt rating film. */
    private HashMap<String, Double> movieRatings;

    /* Pt rating serial. -> nume serial, numar sezon, rating*/
    private Map<String, Map<Integer, Double>> serialRatings;

    /* Constructor special. */
    public User(UserInputData user) {
        this.username = user.getUsername();
        this.subscriptionType = user.getSubscriptionType();
        this.history = user.getHistory();
        this.favoriteMovies = user.getFavoriteMovies();
        this.movieRatings = new HashMap<>();
        this.serialRatings = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public void setHistory(Map<String, Integer> history) {
        this.history = history;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void setFavoriteMovies(ArrayList<String> favoriteMovies) {
        this.favoriteMovies = favoriteMovies;
    }

    public Map<String, Double> getMovieRatings() {
        return movieRatings;
    }

    public void setMovieRatings(HashMap<String, Double> movieRatings) {
        this.movieRatings = movieRatings;
    }

    public Map<String, Map<Integer, Double>> getSerialRatings() {
        return serialRatings;
    }

    public void setSerialRatings(Map<String, Map<Integer, Double>> serialRatings) {
        this.serialRatings = serialRatings;
    }

    @Override
    public String toString() {
        return "UserInputData{" + "username='"
                + username + '\'' + ", subscriptionType='"
                + subscriptionType + '\'' + ", history="
                + history + ", favoriteMovies="
                + favoriteMovies + '}';
    }
}
