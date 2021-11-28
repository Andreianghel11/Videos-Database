package actions;

import database.Action;
import database.Database;
import database.Output;
import database.User;

import java.io.IOException;

public class ActionExecutor {
    public static void executeCommand(Database database, Action action, Output output) throws IOException {
        if (action.getType().equals("favorite")) {
            User user = database.getUsersMap().get(action.getUsername());

            if (user.getHistory().containsKey(action.getTitle()) == false) {
                output.invalidFavoriteMessage(action.getActionId(), action.getTitle());
                return;
            }

            if (user.getFavoriteMovies().contains(action.getTitle())) {
                output.duplicateFavoriteMessage(action.getActionId(), action.getTitle());
                return;
            }

            user.getFavoriteMovies().add(action.getTitle());
            output.addFavoriteMessage(action.getActionId(), action.getTitle());
        }

    }
}
