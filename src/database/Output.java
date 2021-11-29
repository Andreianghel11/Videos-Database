package database;

import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Output {
    private Writer fileWriter;
    private JSONArray arrayResult;

    public Output(Writer fileWriter, JSONArray arrayResult) {
        this.fileWriter = fileWriter;
        this.arrayResult = arrayResult;
    }

    public void displayFavoriteMessage(int id, String title) throws IOException {
        String message = "success -> " + title + " was added as favourite";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    public void displayDuplicateFavoriteMessage(int id, String title) throws IOException {
        String message = "error -> " + title + " is already in favourite list";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    public void displayViewMessage(int id, String title, int views) throws IOException {
        String message = "success -> " + title + " was viewed with total views of " + Integer.toString(views);
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    public void displayRatingMessage(int id, String title, double rating, String user) throws IOException {
        String message = "success -> " + title + " was rated with " + Double.toString(rating) + " by " + user;
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    public void displayErrorAlreadyRated(int id, String title) throws IOException {
        String message = "error -> " + title + " has been already rated";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    public void displayErrorNoTitle(int id, String title) throws IOException {
        String message = "error -> " + title + " is not seen";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /* TO BE TESTED*/
    public void displayQueryResult(int id, List<String> list) throws IOException {
        String message ="Query result: " + list;
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

}
