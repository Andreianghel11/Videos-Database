package database;

import entertainment.Genre;
import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.Input;
import fileio.MovieInputData;
import fileio.SerialInputData;
import fileio.UserInputData;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Clasa definește obiectele de tipul Database. Un obiect database conține
 * toate informațiile primite ca input. Actorii, utilizatorii, filmele și
 * serialele sunt reținute sub formă de perechi nume - referință obiect în
 * cadrul unor structuri de tip HashMap, iar acțiunile sunt reținute în
 * cadrul unei structuri de tip ArrayList.
 */
public final class Database {
    private HashMap<String, Actor> actorsMap;

    private HashMap<String, User> usersMap;

    private HashMap<String, Movie> moviesMap;

    private HashMap<String, Serial> serialsMap;

    private ArrayList<Action> actionsList;

    /**
     * Constructor specializat.
     */
    public Database(final Input input) {
        actorsMap = new HashMap<>();
        for (ActorInputData currentActor : input.getActors()) {
            Actor newActor = new Actor(currentActor);
            actorsMap.put(newActor.getName(), newActor);
        }

        usersMap = new HashMap<>();
        for (UserInputData currentUser : input.getUsers()) {
            User newUser = new User(currentUser);
            usersMap.put(newUser.getUsername(), newUser);
        }

        int showId = 1;
        moviesMap = new LinkedHashMap<>();
        for (MovieInputData currentMovie : input.getMovies()) {
            Movie newMovie = new Movie(currentMovie);
            newMovie.setId(showId++);
            moviesMap.put(newMovie.getTitle(), newMovie);
        }

        serialsMap = new LinkedHashMap<>();
        for (SerialInputData currentSerial : input.getSerials()) {
            Serial newSerial = new Serial(currentSerial);
            newSerial.setId(showId++);
            serialsMap.put(newSerial.getTitle(), newSerial);
        }

        actionsList = new ArrayList<>();
        for (ActionInputData currentAction : input.getCommands()) {
            Action newAction = new Action(currentAction);
            actionsList.add(newAction);
        }
    }

    public HashMap<String, Actor> getActorsMap() {
        return actorsMap;
    }

    public void setActorsMap(final HashMap<String, Actor> actorsMap) {
        this.actorsMap = actorsMap;
    }

    public HashMap<String, User> getUsersMap() {
        return usersMap;
    }

    public void setUsersMap(final HashMap<String, User> usersMap) {
        this.usersMap = usersMap;
    }

    public HashMap<String, Movie> getMoviesMap() {
        return moviesMap;
    }

    public void setMoviesMap(final HashMap<String, Movie> moviesMap) {
        this.moviesMap = moviesMap;
    }

    public HashMap<String, Serial> getSerialsMap() {
        return serialsMap;
    }

    public void setSerialsMap(final HashMap<String, Serial> serialsMap) {
        this.serialsMap = serialsMap;
    }

    public ArrayList<Action> getActionsList() {
        return actionsList;
    }

    public void setActionsList(final ArrayList<Action> actionsList) {
        this.actionsList = actionsList;
    }

    /**
     * Calculează numărul de vizionări al unui gen primit ca parametru.
     */
    public int calculateGenreViews(final Genre genre) {
        int numberOfViews = 0;
        for (Movie currentMovie : this.getMoviesMap().values()) {
            for (String currentGenre : currentMovie.getGenres()) {
                if (Utils.stringToGenre(currentGenre).equals(genre)) {
                    numberOfViews += currentMovie.getNrOfViews();
                }
            }
        }
        return numberOfViews;
    }

    /**
     * Actualizeză baza de date și calculează numărul de adăugări
     * în listele de favorite pentru fiecare show.
     */
    public void calculateNumberOfFavoritesForEachShow() {
        List<Show> showList = createShowList();
        for (Show currentShow : showList) {
            currentShow.numberOfFavorites(this);
        }
    }

    /**
     * Actualizează baza de date și calculează numărul de
     * vizualizări pentru fiecare show.
     */
    public void calculateNumberOfViewsForEveryShow() {
        List<Show> showList = createShowList();
        for (Show currentShow : showList) {
            currentShow.numberOfViews(this);
        }
    }

    /**
     * Actualizează baza de date și calculează
     * rating-ul fiecărui actor.
     */
    public void calculateRatingForEveryActor() {
        for (Actor currentActor : this.getActorsMap().values()) {
            currentActor.calculateActorRating(this);
        }
    }

    /**
     * Returnează o listă de show-uri formată din
     * lista de filme și cea de seriale a bazei de date.
     */
    public List<Show> createShowList() {
        List<Show> showList = new ArrayList<>();

        showList.addAll(moviesMap.values());
        showList.addAll(serialsMap.values());

        return showList;
    }
}
