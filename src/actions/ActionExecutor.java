package actions;

import database.Action;
import database.Actor;
import database.Database;
import database.FilterMethods;
import database.Movie;
import database.Output;
import database.Serial;
import database.Show;
import database.User;
import entertainment.Genre;
import entertainment.Season;
import utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Clasa are ca scop executarea efectivă a acțiunilor primite ca input.
 * Aici se află mare parte din logica programului.
 */
public final class ActionExecutor {

    private ActionExecutor() {
    }

    /**
     * Apelează metoda corespunzătoare în funcție de
     * tipul acțiunii - comandă, query sau recomandare.
     */
    public static void actionSelector(final Database database, final Action action,
                                      final Output output) throws IOException {
        switch (action.getActionType()) {
            case "command" -> ActionExecutor.executeCommand(database, action, output);
            case "query" -> ActionExecutor.executeQuerry(database, action, output);
            case "recommendation" -> ActionExecutor.executeRecommendation(database, action, output);
            default -> throw new IllegalStateException("Unexpected value: "
                    + action.getActionType());
        }
    }

    /**
     * Executarea acțiunilor de tip comandă. Sunt tratate cele 3 tipuri de comenzi.
     */
    public static void executeCommand(final Database database, final Action action,
                                      final Output output) throws IOException {
        User user = database.getUsersMap().get(action.getUsername());

        switch (action.getType()) {
            case "favorite" -> {
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
            }

            case "view" -> {
                int views = 0;

                if (user.getHistory().containsKey(action.getTitle())) {
                    views = user.getHistory().get(action.getTitle());
                    views++;
                } else {
                    views = 1;
                }
                user.getHistory().put(action.getTitle(), views);
                output.displayViewMessage(action.getActionId(), action.getTitle(), views);
            }

            case "rating" -> {
                if (!user.getHistory().containsKey(action.getTitle())) {
                    output.displayErrorNoTitle(action.getActionId(), action.getTitle());
                } else if (user.getMovieRatings() != null
                        && user.getMovieRatings().containsKey(action.getTitle())
                        || user.getSerialRatings().get(action.getTitle()) != null
                        && user.getSerialRatings().get(action.getTitle())
                        .containsKey(action.getSeasonNumber())) {
                    output.displayErrorAlreadyRated(action.getActionId(), action.getTitle());
                } else {
                    /* Cazul pentru filme. */
                    if (action.getSeasonNumber() == 0) {
                        user.getMovieRatings().put(action.getTitle(), action.getGrade());
                        output.displayRatingMessage(action.getActionId(), action.getTitle(),
                                action.getGrade(), user.getUsername());

                        /* Noul rating este adaugat in lista de rating-uri a filmului. */
                        Movie currentMovie = database.getMoviesMap().get(action.getTitle());
                        currentMovie.getRatings().add(action.getGrade());
                    } else {
                        /* Cazul pentru seriale. */
                        if (!user.getSerialRatings().containsKey(action.getTitle())) {
                            HashMap<Integer, Double> seasonsRatings = new HashMap<>();
                            seasonsRatings.put(action.getSeasonNumber(), action.getGrade());
                            user.getSerialRatings().put(action.getTitle(), seasonsRatings);
                        } else {
                            user.getSerialRatings().get(action.getTitle())
                                    .put(action.getSeasonNumber(), action.getGrade());
                        }

                        output.displayRatingMessage(action.getActionId(), action.getTitle(),
                                action.getGrade(), user.getUsername());

                        /* Noul rating este adaugat in lista de rating-uri a serialului. */
                        Serial currentSerial = database.getSerialsMap().get(action.getTitle());
                        Season currentSeason = currentSerial.getSeasons()
                                .get(action.getSeasonNumber() - 1);
                        currentSeason.getRatings().add(action.getGrade());
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + action.getType());
        }
    }

    /**
     * Executarea acțiunilor de tip query. Sunt apelate metodele corespunzătoare
     * celor 4 tipuri de query-uri - pentru actori, filme, seriale sau utilizatori.
     */
    public static void executeQuerry(final Database database, final Action action,
                                     final Output output) throws IOException {
        /* Actualizarea bazei de date. */
        database.calculateNumberOfFavoritesForEachShow();
        database.calculateRatingForEveryActor();
        database.calculateNumberOfViewsForEveryShow();

        switch (action.getObjectType()) {
            case "actors" -> executeActorQuery(database, action, output);
            case "movies" -> executeMovieQuery(database, action, output);
            case "shows" -> executeSerialQuery(database, action, output);
            case "users" -> executeUserQuery(database, action, output);
            default -> throw new IllegalStateException("Unexpected value: " + action.getType());
        }
    }

    /**
     * Executarea acțiunilor de tip query pentru actori.
     */
    public static void executeActorQuery(final Database database, final Action action,
                                         final Output output) throws IOException {
        switch (action.getCriteria()) {
            case "average":
                if (action.getSortType().equals("asc")) {
                    List<String> result = database.getActorsMap().values().stream()
                            .sorted(Comparator.comparing(Actor::getName))
                            .sorted(Comparator.comparingDouble(Actor::getActorRating))
                            .filter(a -> a.getActorRating() > 0).limit(action.getNumber())
                            .map(Actor::getName).toList();
                    output.displayQueryResult(action.getActionId(), result);

                } else if (action.getSortType().equals("desc")) {
                    List<String> result = database.getActorsMap().values().stream()
                            .sorted(Comparator.comparing(Actor::getName).reversed())
                            .sorted(Comparator.comparingDouble(Actor::getActorRating).reversed())
                            .filter(a -> a.getActorRating() > 0).limit(action.getNumber())
                            .map(Actor::getName).toList();
                    output.displayQueryResult(action.getActionId(), result);
                }
                break;

            case "awards":
                List<Actor> actorList = new ArrayList<>();
                List<String> awardList = action.getFilters().get(2 + 1);
                for (Actor currentActor : database.getActorsMap().values()) {
                    int actorHasAllAwards = 1;
                    for (String currentAward : awardList) {
                        if (!currentActor.getAwards()
                                .containsKey(Utils.stringToAwards(currentAward))) {
                            actorHasAllAwards = 0;
                            break;
                        }
                    }
                    if (actorHasAllAwards == 1) {
                        actorList.add(currentActor);
                    }
                }
                if (action.getSortType().equals("asc")) {
                    List<String> result = actorList.stream()
                            .sorted(Comparator.comparing(Actor::getName))
                            .sorted(Comparator.comparingInt(Actor::calculateNumberOfAwards))
                            .map(Actor::getName).limit(action.getNumber()).toList();
                    output.displayQueryResult(action.getActionId(), result);

                } else if (action.getSortType().equals("desc")) {
                    List<String> result = actorList.stream()
                            .sorted(Comparator.comparing(Actor::getName).reversed())
                            .sorted(Comparator.comparingInt(Actor::calculateNumberOfAwards)
                                    .reversed()).
                            limit(action.getNumber()).map(Actor::getName).toList();
                    output.displayQueryResult(action.getActionId(), result);
                }
            break;

            case "filter_description":
                List<String> wordList = action.getFilters().get(2);
                List<Actor> currentActorList = FilterMethods.filterActorList(database
                        .getActorsMap().values(), wordList);
                if (action.getSortType().equals("asc")) {
                    List<String> result = currentActorList.stream()
                            .sorted(Comparator.comparing(Actor::getName))
                            .map(Actor::getName).limit(action.getNumber()).toList();
                    output.displayQueryResult(action.getActionId(), result);

                } else if (action.getSortType().equals("desc")) {
                    List<String> result = currentActorList.stream()
                            .sorted(Comparator.comparing(Actor::getName).reversed())
                            .limit(action.getNumber()).map(Actor::getName).toList();
                    output.displayQueryResult(action.getActionId(), result);
                }
            break;
            default:
                throw new IllegalStateException("Unexpected value: " + action.getCriteria());
        }
    }

    /**
     * Executarea acțiunilor de tip query pentru filme.
     */
    public static void executeMovieQuery(final Database database, final Action action,
                                         final Output output) throws IOException {
        switch (action.getCriteria()) {
            case "ratings":
                if (action.getSortType().equals("asc")) {
                    List<String> result = database.getMoviesMap().values().stream()
                            .filter(m -> m.calculateShowGrade() > 0)
                            .filter(m -> m.isFromYear(action.getFilters().get(0)))
                            .filter(m -> m.hasAllGenres(action.getFilters().get(1)))
                            .sorted(Comparator.comparing(Movie::getTitle))
                            .sorted(Comparator.comparingDouble(Movie::calculateShowGrade))
                            .limit(action.getNumber()).map(Movie::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);

                } else if (action.getSortType().equals("desc")) {
                    List<String> result = database.getMoviesMap().values().stream()
                            .filter(m -> m.calculateShowGrade() > 0)
                            .filter(m -> m.isFromYear(action.getFilters().get(0)))
                            .filter(m -> m.hasAllGenres(action.getFilters().get(1)))
                            .sorted(Comparator.comparing(Movie::getTitle).reversed())
                            .sorted(Comparator.comparingDouble(Movie::calculateShowGrade)
                                    .reversed())
                            .limit(action.getNumber()).map(Movie::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);
                }
                break;

            case "favorite":
                if (action.getSortType().equals("asc")) {
                    List<String> result = database.getMoviesMap().values().stream()
                            .filter(m -> m.isFromYear(action.getFilters().get(0)))
                            .filter(m -> m.hasAllGenres(action.getFilters().get(1)))
                            .filter(m -> m.getNrOfFavorites() > 0)
                            .sorted(Comparator.comparing(Movie::getTitle))
                            .sorted(Comparator.comparingInt(Show::getNrOfFavorites))
                            .limit(action.getNumber()).map(Movie::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);

                } else if (action.getSortType().equals("desc")) {
                    List<String> result = database.getMoviesMap().values().stream()
                            .filter(m -> m.isFromYear(action.getFilters().get(0)))
                            .filter(m -> m.hasAllGenres(action.getFilters().get(1)))
                            .filter(m -> m.getNrOfFavorites() > 0)
                            .sorted(Comparator.comparing(Movie::getTitle).reversed())
                            .sorted(Comparator.comparingInt(Show::getNrOfFavorites).reversed())
                            .limit(action.getNumber()).map(Movie::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);
                }
                break;

            case "longest":
                if (action.getSortType().equals("asc")) {
                    List<String> result = database.getMoviesMap().values().stream().
                            filter(m -> m.isFromYear(action.getFilters().get(0)))
                            .filter(m -> m.hasAllGenres(action.getFilters().get(1)))
                            .sorted(Comparator.comparing(Movie::getTitle))
                            .sorted(Comparator.comparingInt(Movie::getDuration))
                            .limit(action.getNumber()).map(Movie::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);

                } else if (action.getSortType().equals("desc")) {
                    List<String> result = database.getMoviesMap().values().stream()
                            .filter(m -> m.isFromYear(action.getFilters().get(0)))
                            .filter(m -> m.hasAllGenres(action.getFilters().get(1)))
                            .sorted(Comparator.comparing(Movie::getTitle).reversed())
                            .sorted(Comparator.comparingInt(Movie::getDuration).reversed())
                            .limit(action.getNumber()).map(Movie::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);
                }
                break;

            case "most_viewed":
                if (action.getSortType().equals("asc")) {
                    List<String> result = database.getMoviesMap().values().stream()
                            .filter(m -> m.isFromYear(action.getFilters().get(0)))
                            .filter(m -> m.hasAllGenres(action.getFilters().get(1)))
                            .filter(m -> m.getNrOfViews() > 0)
                            .sorted(Comparator.comparing(Movie::getTitle))
                            .sorted(Comparator.comparingInt(Movie::getNrOfViews))
                            .limit(action.getNumber()).map(Movie::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);

                } else if (action.getSortType().equals("desc")) {
                    List<String> result = database.getMoviesMap().values().stream()
                            .filter(m -> m.isFromYear(action.getFilters().get(0)))
                            .filter(m -> m.hasAllGenres(action.getFilters().get(1)))
                            .filter(m -> m.getNrOfViews() > 0)
                            .sorted(Comparator.comparing(Movie::getTitle).reversed())
                            .sorted(Comparator.comparingInt(Movie::getNrOfViews).reversed())
                            .limit(action.getNumber()).map(Movie::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + action.getCriteria());
        }
    }

    /**
     * Executarea acțiunilor de tip query pentru seriale.
     */
    public static void executeSerialQuery(final Database database, final Action action,
                                          final Output output) throws IOException {
        switch (action.getCriteria()) {
            case "ratings":
                if (action.getSortType().equals("asc")) {
                    List<String> result = database.getSerialsMap().values().stream()
                            .filter(s -> s.calculateShowGrade() > 0)
                            .filter(s -> s.isFromYear(action.getFilters().get(0)))
                            .filter(s -> s.hasAllGenres(action.getFilters().get(1)))
                            .sorted(Comparator.comparing(Serial::getTitle))
                            .sorted(Comparator.comparingDouble(Serial::calculateShowGrade))
                            .limit(action.getNumber()).map(Serial::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);

                } else if (action.getSortType().equals("desc")) {
                    List<String> result = database.getSerialsMap().values().stream()
                            .filter(s -> s.calculateShowGrade() > 0)
                            .filter(s -> s.isFromYear(action.getFilters().get(0)))
                            .filter(s -> s.hasAllGenres(action.getFilters().get(1)))
                            .sorted(Comparator.comparing(Serial::getTitle).reversed())
                            .sorted(Comparator.comparingDouble(Serial::calculateShowGrade)
                                    .reversed())
                            .limit(action.getNumber()).map(Serial::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);
                }
                break;

            case "favorite":
                if (action.getSortType().equals("asc")) {
                    List<String> result = database.getSerialsMap().values().stream()
                            .filter(s -> s.isFromYear(action.getFilters().get(0)))
                            .filter(s -> s.hasAllGenres(action.getFilters().get(1)))
                            .filter(s -> s.getNrOfFavorites() > 0)
                            .sorted(Comparator.comparing(Serial::getTitle))
                            .sorted(Comparator.comparingInt(Show::getNrOfFavorites))
                            .limit(action.getNumber()).map(Serial::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);

                } else if (action.getSortType().equals("desc")) {
                    List<String> result = database.getSerialsMap().values().stream()
                            .filter(s -> s.isFromYear(action.getFilters().get(0)))
                            .filter(s -> s.hasAllGenres(action.getFilters().get(1)))
                            .filter(s -> s.getNrOfFavorites() > 0)
                            .sorted(Comparator.comparing(Serial::getTitle).reversed())
                            .sorted(Comparator.comparingInt(Show::getNrOfFavorites).reversed())
                            .limit(action.getNumber()).map(Serial::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);
                }
                break;

            case "longest":
                if (action.getSortType().equals("asc")) {
                    List<String> result = database.getSerialsMap().values().stream()
                            .filter(s -> s.isFromYear(action.getFilters().get(0)))
                            .filter(s -> s.hasAllGenres(action.getFilters().get(1)))
                            .sorted(Comparator.comparing(Serial::getTitle))
                            .sorted(Comparator.comparingInt(Serial::calculateDuration))
                            .limit(action.getNumber()).map(Serial::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);

                } else if (action.getSortType().equals("desc")) {
                    List<String> result = database.getSerialsMap().values().stream()
                            .filter(s -> s.isFromYear(action.getFilters().get(0)))
                            .filter(s -> s.hasAllGenres(action.getFilters().get(1)))
                            .sorted(Comparator.comparing(Serial::getTitle).reversed())
                            .sorted(Comparator.comparingInt(Serial::calculateDuration).reversed())
                            .limit(action.getNumber()).map(Serial::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);
                }
                break;

            case "most_viewed":
                if (action.getSortType().equals("asc")) {
                    List<String> result = database.getSerialsMap().values().stream()
                            .filter(s -> s.isFromYear(action.getFilters().get(0)))
                            .filter(s -> s.hasAllGenres(action.getFilters().get(1)))
                            .filter(s -> s.getNrOfViews() > 0)
                            .sorted(Comparator.comparing(Serial::getTitle))
                            .sorted(Comparator.comparingInt(Show::getNrOfViews))
                            .limit(action.getNumber()).map(Serial::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);

                } else if (action.getSortType().equals("desc")) {
                    List<String> result = database.getSerialsMap().values().stream()
                            .filter(s -> s.isFromYear(action.getFilters().get(0)))
                            .filter(s -> s.hasAllGenres(action.getFilters().get(1)))
                            .filter(s -> s.getNrOfViews() > 0)
                            .sorted(Comparator.comparing(Serial::getTitle).reversed())
                            .sorted(Comparator.comparingInt(Show::getNrOfViews).reversed())
                            .limit(action.getNumber()).map(Serial::getTitle).toList();
                    output.displayQueryResult(action.getActionId(), result);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + action.getCriteria());
        }
    }

    /**
     * Executarea acțiunilor de tip query pentru utilizatori.
     */
    public static void executeUserQuery(final Database database, final Action action,
                                        final Output output) throws IOException {
        if (action.getCriteria().equals("num_ratings")) {
            if (action.getSortType().equals("asc")) {
                List<String> result = database.getUsersMap().values().stream()
                        .filter(u -> u.numberOfRatings() > 0)
                        .sorted(Comparator.comparing(User::getUsername))
                        .sorted(Comparator.comparingInt(User::numberOfRatings))
                        .limit(action.getNumber()).map(User::getUsername).toList();
                output.displayQueryResult(action.getActionId(), result);

            } else if (action.getSortType().equals("desc")) {
                List<String> result = database.getUsersMap().values().stream()
                        .filter(u -> u.numberOfRatings() > 0)
                        .sorted(Comparator.comparing(User::getUsername).reversed())
                        .sorted(Comparator.comparingInt(User::numberOfRatings).reversed())
                        .limit(action.getNumber()).map(User::getUsername).toList();
                output.displayQueryResult(action.getActionId(), result);
            }
        }
    }

    /**
     * Executarea acțiunilor de tip recomandare.
     * Sunt tratate cele 5 tipuri de recomandări.
     */
    public static void executeRecommendation(final Database database, final Action action,
                                             final Output output) throws IOException {
        /*Actualizarea bazei de date. */
        database.calculateNumberOfFavoritesForEachShow();
        List<Show> showList = database.createShowList();
        List<String> sortedShowList = new ArrayList<>();
        User user = database.getUsersMap().get(action.getUsername());

        switch (action.getType()) {
            case "standard" -> {
                sortedShowList = showList.stream().sorted(Comparator.comparingInt(Show::getId))
                        .map(Show::getTitle).toList();
                for (String currentShow : sortedShowList) {
                    if (!user.getHistory().containsKey(currentShow)) {
                        output.displayStandardRecommendation(action.getActionId(), currentShow);
                        return;
                    }
                }
                output.displayErrorStandardRecommendation(action.getActionId());
            }
            case "best_unseen" -> {
                sortedShowList = showList.stream().sorted(Comparator.comparingInt(Show::getId)).
                        sorted(Comparator.comparingDouble(Show::calculateShowGrade).reversed())
                        .map(Show::getTitle).toList();
                for (String currentShow : sortedShowList) {
                    if (!user.getHistory().containsKey(currentShow)) {
                        output.displayBestRatedUnseenRecommendation(action.getActionId(),
                                currentShow);
                        return;
                    }
                }
                output.displayErrorBestRatedUnseenRecommendation(action.getActionId());
            }
            case "popular" -> {
                if (user.getSubscriptionType().equals("BASIC")) {
                    output.displayErrorPopularRecommendation(action.getActionId());
                    return;
                }
                List<Genre> genreList = new ArrayList<>();
                Collections.addAll(genreList, Genre.values());
                genreList = genreList.stream()
                        .sorted(Comparator.comparingInt(database::calculateGenreViews).reversed())
                        .toList();

                showList = showList.stream().sorted(Comparator.comparingInt(Show::getId)).toList();
                for (Genre currentGenre : genreList) {
                    for (Show currentShow : showList) {
                        if (!user.getHistory().containsKey(currentShow.getTitle())
                                && currentShow.hasGenre(currentGenre)) {
                            output.displayPopularRecommendation(action.getActionId(),
                                    currentShow.getTitle());
                            return;
                        }
                    }
                }
                output.displayErrorPopularRecommendation(action.getActionId());
            }
            case "favorite" -> {
                if (user.getSubscriptionType().equals("BASIC")) {
                    output.displayErrorPopularRecommendation(action.getActionId());
                    return;
                }
                for (Show currentShow : showList) {
                    currentShow.numberOfFavorites(database);
                }
                sortedShowList = showList.stream().sorted(Comparator.comparingInt(Show::getId)).
                        sorted(Comparator.comparingInt(Show::getNrOfFavorites).reversed()).
                        filter(s -> s.getNrOfFavorites() > 0).
                        map(Show::getTitle).toList();
                for (String currentShow : sortedShowList) {
                    if (!user.getHistory().containsKey(currentShow)) {
                        output.displayFavoriteRecommendation(action.getActionId(), currentShow);
                        return;
                    }
                }
                output.displayErrorFavoriteRecommendation(action.getActionId());
            }
            case "search" -> {
                if (user.getSubscriptionType().equals("BASIC")) {
                    output.displayErrorPopularRecommendation(action.getActionId());
                    return;
                }
                sortedShowList = showList.stream()
                        .filter(s -> s.hasGenre(Utils.stringToGenre(action.getGenre())))
                        .sorted(Comparator.comparing(Show::getTitle))
                        .sorted(Comparator.comparingDouble(Show::calculateShowGrade))
                        .map(Show::getTitle).toList();
                List<String> displayList = new ArrayList<>();
                for (String currentShow : sortedShowList) {
                    if (!user.getHistory().containsKey(currentShow)) {
                        displayList.add(currentShow);
                    }
                }
                if (displayList.isEmpty()) {
                    output.displayErrorSearchRecommendation(action.getActionId());
                } else {
                    output.displaySearchRecommendation(action.getActionId(), displayList);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + action.getType());
        }
    }
}
