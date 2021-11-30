package actions;

import database.*;
import entertainment.Season;
import utils.Utils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

                    Movie currentMovie = database.getMoviesMap().get(action.getTitle());
                    currentMovie.getRatings().add(action.getGrade());
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

                    /* Adaug rating-ul nou la serialul respectiv. */
                    Serial currentSerial = database.getSerialsMap().get(action.getTitle());
                    Season currentSeason = currentSerial.getSeasons().get(action.getSeasonNumber() - 1);
                    currentSeason.getRatings().add(action.getGrade());
                }
            }
        }

    }

    public static void executeQuerry(Database database, Action action, Output output) throws IOException {
        if (action.getObjectType().equals("actors")) {
            if (action.getCriteria().equals("average")) {
                if (action.getSortType().equals("asc")) {
                    List<String> result = database.getActorsMap().values().stream().sorted(Comparator.comparing(Actor::getName)).
                            sorted(Comparator.comparingDouble(a -> a.calculateActorRating(database))).limit(action.getNumber()).
                            map(Actor::getName).toList();
                    output.displayQueryResult(action.getActionId(), result);
                } else if (action.getSortType().equals("desc")) {
                    List<String> result = database.getActorsMap().values().stream().sorted(Comparator.comparing(Actor::getName)).
                            sorted(Comparator.comparingDouble(a -> a.calculateActorRating(database))).map(Actor::getName).
                            toList();
                    ArrayList<String> result2 = new ArrayList<>(result);
                    Collections.reverse(result2);
                    List<String> result3 = new ArrayList<>();
                    if (result2 != null) {
                        if (action.getNumber() <= result2.size())
                            result3 = result2.subList(0, action.getNumber());
                        else
                            result3 = result2;
                    }

                    output.displayQueryResult(action.getActionId(), result3);
                }

            } else if (action.getCriteria().equals("awards")) {
                List<Actor> actorList = new ArrayList<>();
                List<String> awardList = action.getFilters().get(3);
                for (Actor currentActor : database.getActorsMap().values()) {
                    int actorHasAllAwards = 1;
                    for (String currentAward : awardList) {
                        if (!currentActor.getAwards().containsKey(Utils.stringToAwards(currentAward))) {
                            actorHasAllAwards = 0;
                            break;
                        }
                    }
                    if (actorHasAllAwards == 1) {
                        actorList.add(currentActor);
                    }
                }
                if (action.getSortType().equals("asc")) {
                    List<String> result = actorList.stream().sorted(Comparator.comparing(Actor::getName)).
                            sorted(Comparator.comparingInt(Actor::calculateNumberOfAwards)).map(Actor::getName).
                            limit(action.getNumber()).toList();
                    output.displayQueryResult(action.getActionId(), result);
                } else if (action.getSortType().equals("desc")) {
                    List<String> result = actorList.stream().sorted(Comparator.comparing(Actor::getName)).
                            sorted(Comparator.comparingInt(Actor::calculateNumberOfAwards)).map(Actor::getName).toList();
                    ArrayList<String> result2 = new ArrayList<>(result);
                    Collections.reverse(result2);
                    List<String> result3 = new ArrayList<>();
                    if (result2 != null) {
                        if (action.getNumber() <= result2.size())
                            result3 = result2.subList(0, action.getNumber());
                        else
                            result3 = result2;
                    }
                    output.displayQueryResult(action.getActionId(), result3);
                }
            } else if (action.getCriteria().equals("filter_description")) {
                List<String> wordList = action.getFilters().get(2);
                /* De vazut daca merge. Ar putea merge facut si cu stream uri*/
                List<Actor> actorList = FilterMethods.filterActorList(database.getActorsMap().values(), wordList);
                if (action.getSortType().equals("asc")) {
                    List<String> result = actorList.stream().sorted(Comparator.comparing(Actor::getName)).
                            map(Actor::getName).limit(action.getNumber()).toList();
                    output.displayQueryResult(action.getActionId(), result);
                } else if (action.getSortType().equals("desc")) {
                    List<String> result = actorList.stream().sorted(Comparator.comparing(Actor::getName)).
                            map(Actor::getName).toList();
                    ArrayList<String> result2 = new ArrayList<>(result);
                    Collections.reverse(result2);
                    List<String> result3 = new ArrayList<>();
                    if (result2 != null) {
                        if (action.getNumber() <= result2.size())
                            result3 = result2.subList(0, action.getNumber());
                    }
                    output.displayQueryResult(action.getActionId(), result3);
                }
            }
        } else if (action.getObjectType().equals("movies") && action.getCriteria().equals("ratings")) {
            if (action.getSortType().equals("asc")) {
                List<String> result = database.getMoviesMap().values().stream().filter(m -> m.calculateShowGrade() > 0).
                        filter(m -> m.isFromYear(action.getFilters().get(0))).filter(m -> m.hasGenres(action.getFilters().get(1))).
                        sorted(Comparator.comparing(Movie::getTitle)).sorted(Comparator.comparingDouble(Movie::calculateShowGrade)).
                        limit(action.getNumber()).map(Movie::getTitle).toList();

                output.displayQueryResult(action.getActionId(), result);
            } else if (action.getSortType().equals("desc")) {
                List<String> result = database.getMoviesMap().values().stream().filter(m -> m.calculateShowGrade() > 0).
                        filter(m -> m.isFromYear(action.getFilters().get(0))).filter(m -> m.hasGenres(action.getFilters().get(1))).
                        sorted(Comparator.comparing(Movie::getTitle)).sorted(Comparator.comparingDouble(Movie::calculateShowGrade)).
                        map(Movie::getTitle).toList();

                ArrayList<String> result2 = new ArrayList<>(result);
                Collections.reverse(result2);
                List<String> result3 = new ArrayList<>();
                if (result2 != null) {
                    if (action.getNumber() <= result2.size())
                        result3 = result2.subList(0, action.getNumber());
                    else
                        result3 = result2;
                }
                output.displayQueryResult(action.getActionId(), result3);
            }
        } else if (action.getObjectType().equals("shows") && action.getCriteria().equals("ratings")) {
            if (action.getSortType().equals("asc")) {
                List<String> result = database.getSerialsMap().values().stream().filter(s -> s.calculateShowGrade() > 0).
                        filter(s -> s.isFromYear(action.getFilters().get(0))).filter(s -> s.hasGenres(action.getFilters().get(1))).
                        sorted(Comparator.comparing(Serial::getTitle)).sorted(Comparator.comparingDouble(Serial::calculateShowGrade)).
                        limit(action.getNumber()).map(Serial::getTitle).toList();

                output.displayQueryResult(action.getActionId(), result);
            } else if (action.getSortType().equals("desc")) {
                List<String> result = database.getSerialsMap().values().stream().filter(s -> s.calculateShowGrade() > 0).
                        filter(s -> s.isFromYear(action.getFilters().get(0))).filter(s -> s.hasGenres(action.getFilters().get(1))).
                        sorted(Comparator.comparing(Serial::getTitle)).sorted(Comparator.comparingDouble(Serial::calculateShowGrade)).
                        map(Serial::getTitle).toList();

                ArrayList<String> result2 = new ArrayList<>(result);
                Collections.reverse(result2);
                List<String> result3 = new ArrayList<>();
                if (result2 != null) {
                    if (action.getNumber() <= result2.size())
                        result3 = result2.subList(0, action.getNumber());
                    else
                        result3 = result2;
                }
                output.displayQueryResult(action.getActionId(), result3);
            }
        } else if (action.getObjectType().equals("movies") && action.getCriteria().equals("favorite")) {
            if (action.getSortType().equals("asc")) {
                List<String> result = database.getMoviesMap().values().stream().
                        filter(m -> m.isFromYear(action.getFilters().get(0))).filter(m -> m.hasGenres(action.getFilters().get(1))).
                        filter(m -> m.numberOfFavorites(database) > 0).sorted(Comparator.comparing(Movie::getTitle)).
                        sorted(Comparator.comparingInt(m -> m.numberOfFavorites(database))).
                        limit(action.getNumber()).map(Movie::getTitle).toList();

                output.displayQueryResult(action.getActionId(), result);
            } else if (action.getSortType().equals("desc")) {
                List<String> result = database.getMoviesMap().values().stream().
                        filter(m -> m.isFromYear(action.getFilters().get(0))).filter(m -> m.hasGenres(action.getFilters().get(1))).
                        filter(m -> m.numberOfFavorites(database) > 0).sorted(Comparator.comparing(Movie::getTitle)).
                        sorted(Comparator.comparingInt(m -> m.numberOfFavorites(database))).
                        map(Movie::getTitle).toList();

                ArrayList<String> result2 = new ArrayList<>(result);
                Collections.reverse(result2);
                List<String> result3 = new ArrayList<>();
                if (result2 != null) {
                    if (action.getNumber() <= result2.size())
                        result3 = result2.subList(0, action.getNumber());
                    else
                        result3 = result2;
                }
                output.displayQueryResult(action.getActionId(), result3);
            }
        } else if (action.getObjectType().equals("shows") && action.getCriteria().equals("favorite")) {
            if (action.getSortType().equals("asc")) {
                List<String> result = database.getSerialsMap().values().stream().
                        filter(s -> s.isFromYear(action.getFilters().get(0))).filter(s -> s.hasGenres(action.getFilters().get(1))).
                        filter(s -> s.numberOfFavorites(database) > 0).sorted(Comparator.comparing(Serial::getTitle)).
                        sorted(Comparator.comparingInt(m -> m.numberOfFavorites(database))).
                        limit(action.getNumber()).map(Serial::getTitle).toList();

                output.displayQueryResult(action.getActionId(), result);
            } else if (action.getSortType().equals("desc")) {
                List<String> result = database.getSerialsMap().values().stream().
                        filter(s -> s.isFromYear(action.getFilters().get(0))).filter(s -> s.hasGenres(action.getFilters().get(1))).
                        filter(s -> s.numberOfFavorites(database) > 0).sorted(Comparator.comparing(Serial::getTitle)).
                        sorted(Comparator.comparingInt(m -> m.numberOfFavorites(database))).
                        map(Serial::getTitle).toList();

                ArrayList<String> result2 = new ArrayList<>(result);
                Collections.reverse(result2);
                List<String> result3 = new ArrayList<>();
                if (result2 != null) {
                    if (action.getNumber() <= result2.size())
                        result3 = result2.subList(0, action.getNumber());
                    else
                        result3 = result2;
                }
                output.displayQueryResult(action.getActionId(), result3);
            }
        } else if (action.getObjectType().equals("movies") && action.getCriteria().equals("longest")) {
            if (action.getSortType().equals("asc")) {
                List<String> result = database.getMoviesMap().values().stream().
                        filter(m -> m.isFromYear(action.getFilters().get(0))).filter(m -> m.hasGenres(action.getFilters().get(1))).
                        sorted(Comparator.comparing(Movie::getTitle)).
                        sorted(Comparator.comparingInt(Movie::getDuration)).
                        limit(action.getNumber()).map(Movie::getTitle).toList();

                output.displayQueryResult(action.getActionId(), result);
            } else if (action.getSortType().equals("desc")) {
                List<String> result = database.getMoviesMap().values().stream().
                        filter(m -> m.isFromYear(action.getFilters().get(0))).filter(m -> m.hasGenres(action.getFilters().get(1)))
                        .sorted(Comparator.comparing(Movie::getTitle)).
                        sorted(Comparator.comparingInt(Movie::getDuration)).
                        map(Movie::getTitle).toList();

                ArrayList<String> result2 = new ArrayList<>(result);
                Collections.reverse(result2);
                List<String> result3 = new ArrayList<>();
                if (result2 != null) {
                    if (action.getNumber() <= result2.size())
                        result3 = result2.subList(0, action.getNumber());
                    else
                        result3 = result2;
                }
                output.displayQueryResult(action.getActionId(), result3);
            }
        } else if (action.getObjectType().equals("shows") && action.getCriteria().equals("longest")) {
            if (action.getSortType().equals("asc")) {
                List<String> result = database.getSerialsMap().values().stream().
                        filter(s -> s.isFromYear(action.getFilters().get(0))).filter(s -> s.hasGenres(action.getFilters().get(1))).
                        sorted(Comparator.comparing(Serial::getTitle)).
                        sorted(Comparator.comparingInt(Serial::calculateDuration)).
                        limit(action.getNumber()).map(Serial::getTitle).toList();

                output.displayQueryResult(action.getActionId(), result);
            } else if (action.getSortType().equals("desc")) {
                List<String> result = database.getSerialsMap().values().stream().
                        filter(s -> s.isFromYear(action.getFilters().get(0))).filter(s -> s.hasGenres(action.getFilters().get(1))).
                        sorted(Comparator.comparing(Serial::getTitle)).
                        sorted(Comparator.comparingInt(Serial::calculateDuration)).
                        map(Serial::getTitle).toList();

                ArrayList<String> result2 = new ArrayList<>(result);
                Collections.reverse(result2);
                List<String> result3 = new ArrayList<>();
                if (result2 != null) {
                    if (action.getNumber() <= result2.size())
                        result3 = result2.subList(0, action.getNumber());
                    else
                        result3 = result2;
                }
                output.displayQueryResult(action.getActionId(), result3);
            }
        }
    }
}