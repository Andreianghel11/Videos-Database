package database;

import actions.ActionExecutor;
import entertainment.Genre;
import fileio.*;
import utils.Utils;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.*;

public class Database {
    /* Nume, actor. */
    private HashMap<String, Actor> actorsMap;

    /* Nume, user. */
    private HashMap<String, User> usersMap;

    /* Nume, film. */
    private HashMap<String, Movie> moviesMap;

    /* Nume, serial. */
    private HashMap<String, Serial> serialsMap;

    private ArrayList<Action> actionsList;

    /* Constructor special pentru input + alocare memorie.*/
    public Database(Input input) {
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

    public void setActorsMap(HashMap<String, Actor> actorsMap) {
        this.actorsMap = actorsMap;
    }

    public HashMap<String, User> getUsersMap() {
        return usersMap;
    }

    public void setUsersMap(HashMap<String, User> usersMap) {
        this.usersMap = usersMap;
    }

    public HashMap<String, Movie> getMoviesMap() {
        return moviesMap;
    }

    public void setMoviesMap(HashMap<String, Movie> moviesMap) {
        this.moviesMap = moviesMap;
    }

    public HashMap<String, Serial> getSerialsMap() {
        return serialsMap;
    }

    public void setSerialsMap(HashMap<String, Serial> serialsMap) {
        this.serialsMap = serialsMap;
    }

    public ArrayList<Action> getActionsList() {
        return actionsList;
    }

    public void setActionsList(ArrayList<Action> actionsList) {
        this.actionsList = actionsList;
    }

    public void actionSelector(Action action, Output output) throws IOException {
        if (action.getActionType().equals("command")) {
            ActionExecutor.executeCommand(this, action, output);
        } else if (action.getActionType().equals("query")) {
            ActionExecutor.executeQuerry(this, action, output);
        } else if (action.getActionType().equals("recommendation")) {
            ActionExecutor.executeRecommendation(this, action, output);
        }
    }

    public int calculateGenreViews(Genre genre) {
        int numberOfViews = 0;
        for (Movie currentMovie : this.getMoviesMap().values()) {
            for (String currentGenre : currentMovie.getGenres()) {
                if (Utils.stringToGenre(currentGenre).equals(genre))
                    numberOfViews += currentMovie.getNrOfViews();
            }

        }
        return numberOfViews;
    }

    public void calculateNumberOfFavoritesForEachShow() {
        List<Show> showList = createShowList();
        for (Show currentShow : showList) {
            currentShow.numberOfFavorites(this);
        }
    }

    public void calculateNumberOfViewsForEveryShow() {
        List<Show> showList = createShowList();
        for (Show currentShow : showList) {
            currentShow.numberOfViews(this);
        }
    }

    public void calculateRatingForEveryActor() {
        for (Actor currentActor : this.getActorsMap().values()) {
            currentActor.calculateActorRating(this);
        }
    }

    public List<Show> createShowList() {
        List<Show> showList = new ArrayList<>();

        showList.addAll(moviesMap.values());
        showList.addAll(serialsMap.values());

        return showList;
    }
}
