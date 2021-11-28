package database;

import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;


public class Output {
    private Writer fileWriter;
    private JSONArray arrayResult;

    public Output(Writer fileWriter, JSONArray arrayResult) {
        this.fileWriter = fileWriter;
        this.arrayResult = arrayResult;
    }

    public void addFavoriteMessage(int id, String message) throws IOException {
        String finalMessage = "success -> " + message + " was added as favourite";
        arrayResult.add(fileWriter.writeFile(id, null, finalMessage));
    }

    public void invalidFavoriteMessage(int id, String message) throws IOException {
        String finalMessage = "error -> " + message + " is not seen";
        arrayResult.add(fileWriter.writeFile(id, null, finalMessage));
    }

    public void duplicateFavoriteMessage(int id, String message) throws IOException {
        String finalMessage = "error -> " + message + " is already in favourite list";
        arrayResult.add(fileWriter.writeFile(id, null, finalMessage));
    }
}
