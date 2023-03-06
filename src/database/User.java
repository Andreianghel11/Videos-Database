package database;

import fileio.UserInputData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clasa definește obiectele de tip utilizator.
 */
public final class User {
    private String username;

    private String subscriptionType;

    private Map<String, Integer> history;

    private ArrayList<String> favoriteMovies;

    private HashMap<String, Double> movieRatings;

    private Map<String, Map<Integer, Double>> serialRatings;

    /**
     * Constructor specializat.
     */
    public User(final UserInputData user) {
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

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(final String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public void setHistory(final Map<String, Integer> history) {
        this.history = history;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void setFavoriteMovies(final ArrayList<String> favoriteMovies) {
        this.favoriteMovies = favoriteMovies;
    }

    public Map<String, Double> getMovieRatings() {
        return movieRatings;
    }

    public void setMovieRatings(final HashMap<String, Double> movieRatings) {
        this.movieRatings = movieRatings;
    }

    public Map<String, Map<Integer, Double>> getSerialRatings() {
        return serialRatings;
    }

    public void setSerialRatings(final Map<String, Map<Integer, Double>> serialRatings) {
        this.serialRatings = serialRatings;
    }

    /**
     * Metoda returnează numărul total de
     * rating-uri acordate de către utilizator.
     */
    public int numberOfRatings() {
        return movieRatings.size() + serialRatings.size();
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
