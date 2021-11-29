package actions;

import database.Action;
import database.Database;
import database.Output;
import database.User;
import entertainment.Season;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActionExecutor {
    public static void executeCommand(Database database, Action action, Output output) throws IOException {
        if (action.getType().equals("favorite")) {
            User user = database.getUsersMap().get(action.getUsername());

            if (!user.getHistory().containsKey(action.getTitle())) {
                output.displayErrorNoTitle(action.getActionId(), action.getTitle());
                return;
            }

            if (user.getFavoriteMovies().contains(action.getTitle())) {
                output.displayDuplicateFavoriteMessage(action.getActionId(), action.getTitle());
                return;
            }

            user.getFavoriteMovies().add(action.getTitle());
            output.displayFavoriteMessage(action.getActionId(), action.getTitle());

        } else if (action.getType().equals("view")) {
            User user = database.getUsersMap().get(action.getUsername());
            int views = 0;

            if (user.getHistory().containsKey(action.getTitle())) {
                views = user.getHistory().get(action.getTitle());
                views++;
            } else {
                views = 1;
            }
            user.getHistory().put(action.getTitle(), views);
            output.displayViewMessage(action.getActionId(), action.getTitle(), views);

        } else if (action.getType().equals("rating")) {
            User user = database.getUsersMap().get(action.getUsername());
            if (!user.getHistory().containsKey(action.getTitle())) {
                output.displayErrorNoTitle(action.getActionId(), action.getTitle());
            } else if (user.getMovieRatings() != null && user.getMovieRatings().containsKey(action.getTitle()) ||
                    user.getSerialRatings().get(action.getTitle()) != null && user.getSerialRatings().get(action.getTitle()).containsKey(action.getSeasonNumber())) {
                output.displayErrorAlreadyRated(action.getActionId(), action.getTitle());
            } else {
                /* Cazul pt filme. */
                if (action.getSeasonNumber() == 0) {
                    user.getMovieRatings().put(action.getTitle(), action.getGrade());
                    output.displayRatingMessage(action.getActionId(), action.getTitle(), action.getGrade(), user.getUsername());
                    /* Cazul pt seriale. */
                } else {
                    if (!user.getSerialRatings().containsKey(action.getTitle())) {
                        HashMap<Integer, Double> seasonsRatings = new HashMap<>();
                        seasonsRatings.put(action.getSeasonNumber(), action.getGrade());
                        user.getSerialRatings().put(action.getTitle(), seasonsRatings);
                    } else {
                        user.getSerialRatings().get(action.getTitle()).put(action.getSeasonNumber(), action.getGrade());
                    }
                    output.displayRatingMessage(action.getActionId(), action.getTitle(), action.getGrade(), user.getUsername());
                }
            }

        }
    }
}