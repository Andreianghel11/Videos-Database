package database;

import actions.ActionExecutor;
import fileio.*;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        moviesMap = new HashMap<>();
        for (MovieInputData currentMovie : input.getMovies()) {
            Movie newMovie = new Movie(currentMovie);
            moviesMap.put(newMovie.getTitle(), newMovie);
        }

        serialsMap = new HashMap<>();
        for (SerialInputData currentSerial : input.getSerials()) {
            Serial newSerial = new Serial(currentSerial);
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

        } else if (action.getActionType().equals("")) {

        }
    }
}
