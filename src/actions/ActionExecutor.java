package actions;

import database.*;
import entertainment.Genre;
import entertainment.Season;
import utils.Utils;

import java.io.IOException;
import java.util.*;

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
        } else if (action.getObjectType().equals("movies") && action.getCriteria().equals("most_viewed")) {
            if (action.getSortType().equals("asc")) {
                List<String> result = database.getMoviesMap().values().stream().
                        filter(m -> m.isFromYear(action.getFilters().get(0))).filter(m -> m.hasGenres(action.getFilters().get(1))).
                        filter(m -> m.numberOfViews(database) > 0).sorted(Comparator.comparing(Movie::getTitle)).
                        sorted(Comparator.comparingInt(m -> m.numberOfViews(database))).
                        limit(action.getNumber()).map(Movie::getTitle).toList();

                output.displayQueryResult(action.getActionId(), result);
            } else if (action.getSortType().equals("desc")) {
                List<String> result = database.getMoviesMap().values().stream().
                        filter(m -> m.isFromYear(action.getFilters().get(0))).filter(m -> m.hasGenres(action.getFilters().get(1))).
                        filter(m -> m.numberOfViews(database) > 0).sorted(Comparator.comparing(Movie::getTitle)).
                        sorted(Comparator.comparingInt(m -> m.numberOfViews(database))).
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
        } else if (action.getObjectType().equals("shows") && action.getCriteria().equals("most_viewed")) {
            if (action.getSortType().equals("asc")) {
                List<String> result = database.getSerialsMap().values().stream().
                        filter(s -> s.isFromYear(action.getFilters().get(0))).filter(s -> s.hasGenres(action.getFilters().get(1))).
                        filter(s -> s.numberOfViews(database) > 0).sorted(Comparator.comparing(Serial::getTitle)).
                        sorted(Comparator.comparingInt(m -> m.numberOfViews(database))).
                        limit(action.getNumber()).map(Serial::getTitle).toList();

                output.displayQueryResult(action.getActionId(), result);
            } else if (action.getSortType().equals("desc")) {
                List<String> result = database.getSerialsMap().values().stream().
                        filter(s -> s.isFromYear(action.getFilters().get(0))).filter(s -> s.hasGenres(action.getFilters().get(1))).
                        filter(s -> s.numberOfViews(database) > 0).sorted(Comparator.comparing(Serial::getTitle)).
                        sorted(Comparator.comparingInt(m -> m.numberOfViews(database))).
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
        } else if (action.getObjectType().equals("users") && action.getCriteria().equals("num_ratings")) {
            if (action.getSortType().equals("asc")) {
                List<String> result = database.getUsersMap().values().stream().
                        filter(u -> u.numberOfRatings() > 0).sorted(Comparator.comparing(User::getUsername)).
                        sorted(Comparator.comparingInt(User::numberOfRatings)).
                        limit(action.getNumber()).map(User::getUsername).toList();

                output.displayQueryResult(action.getActionId(), result);
            } else if (action.getSortType().equals("desc")) {
                List<String> result = database.getUsersMap().values().stream().
                        filter(u -> u.numberOfRatings() > 0).sorted(Comparator.comparing(User::getUsername)).
                        sorted(Comparator.comparingInt(User::numberOfRatings)).
                        map(User::getUsername).toList();

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

    public static void executeRecommendation(Database database, Action action, Output output) throws IOException {
        if (action.getType().equals("standard")) {
            for (String currentMovie : database.getMoviesMap().keySet()) {
                if (!database.getUsersMap().get(action.getUsername()).getHistory().containsKey(currentMovie)) {
                    output.displayStandardRecommendation(action.getActionId(), currentMovie);
                    return;
                }
            }
                for (String currentSerial : database.getSerialsMap().keySet()) {
                    if (!database.getUsersMap().get(action.getUsername()).getHistory().containsKey(currentSerial)) {
                        output.displayStandardRecommendation(action.getActionId(), currentSerial);
                        return;
                    }
                }
            output.displayErrorStandardRecommendation(action.getActionId());
        } else if (action.getType().equals("best_unseen")) {
            List<String> sortedMovieList = database.getMoviesMap().values().stream().
                    sorted(Comparator.comparingDouble(Movie::calculateShowGrade)).map(Movie::getTitle).toList();
            List<String> reversedSortedMovieList = new ArrayList<>(sortedMovieList);
            Collections.reverse(reversedSortedMovieList);

            List<String> sortedSerialList = database.getSerialsMap().values().stream().
                    sorted(Comparator.comparingDouble(Serial::calculateShowGrade)).map(Serial::getTitle).toList();
            List<String> reversedSortedSerialList = new ArrayList<>(sortedSerialList);
            Collections.reverse(reversedSortedSerialList);

            int foundMovie = 0;
            String foundMovieTitle = null;
            for (String currentMovie : reversedSortedMovieList) {
                if (!database.getUsersMap().get(action.getUsername()).getHistory().containsKey(currentMovie)) {
                    foundMovieTitle = currentMovie;
                    foundMovie = 1;
                    break;
                }
            }
            int foundSerial = 0;
            String foundSerialTitle = null;
            for (String currentSerial : reversedSortedSerialList) {
                if (!database.getUsersMap().get(action.getUsername()).getHistory().containsKey(currentSerial)) {
                    foundSerialTitle = currentSerial;
                    foundSerial = 1;
                    break;
                }
            }
            if (foundSerial == 0 && foundMovie == 1)
                output.displayBestRatedUnseenRecommendation(action.getActionId(), foundMovieTitle);
            else if (foundMovie == 0 && foundSerial == 1)
                output.displayBestRatedUnseenRecommendation(action.getActionId(), foundSerialTitle);
            else if (foundMovie == 1 && foundSerial == 1) {
                if (database.getSerialsMap().get(foundSerialTitle).calculateShowGrade() >
                    database.getMoviesMap().get(foundMovieTitle).calculateShowGrade())
                    output.displayBestRatedUnseenRecommendation(action.getActionId(), foundSerialTitle);
                else
                    output.displayBestRatedUnseenRecommendation(action.getActionId(), foundMovieTitle);
            } else
                output.displayErrorBestRatedUnseenRecommendation(action.getActionId());

        } else if (action.getType().equals("popular")) {
            User user = database.getUsersMap().get(action.getUsername());
            if (user.getSubscriptionType().equals("BASIC")) {
                output.displayErrorPopularRecommendation(action.getActionId());
            } else {
                List<Genre> genreList = new ArrayList<>();
                for (Genre currentGenre : Genre.values()) {
                    genreList.add(currentGenre);
                }
                genreList = genreList.stream().sorted(Comparator.comparingInt(g -> database.calculateGenreViews(g))).
                        toList();
                List<Genre> reversedGenreList = new ArrayList<>(genreList);
                Collections.reverse(reversedGenreList);

                for (Genre currentGenre : reversedGenreList) {
                    for (Movie currentMovie : database.getMoviesMap().values()) {
                        if (!user.getHistory().containsKey(currentMovie.getTitle()) && currentMovie.hasGenre(currentGenre)) {
                            output.displayPopularRecommendation(action.getActionId(), currentMovie.getTitle());
                            return;
                        }
                    }
                    for (Serial currentSerial : database.getSerialsMap().values()) {
                        if (!user.getHistory().containsKey(currentSerial.getTitle()) && currentSerial.hasGenre(currentGenre)) {
                            output.displayPopularRecommendation(action.getActionId(), currentSerial.getTitle());
                            return;
                        }
                    }
                }
                output.displayErrorPopularRecommendation(action.getActionId());
            }
        } else if (action.getType().equals("favorite")) {
            User user = database.getUsersMap().get(action.getUsername());
            if (user.getSubscriptionType().equals("BASIC")) {
                output.displayErrorPopularRecommendation(action.getActionId());
            } else {
                List<String> moviesList = database.getMoviesMap().values().stream().
                        filter(m -> m.numberOfFavorites(database) > 0).
                        sorted(Comparator.comparingInt(m -> m.numberOfFavorites(database))).
                        map(Movie::getTitle).toList();
                List<String> reversedMoviesList = new ArrayList<>(moviesList);
                Collections.reverse(reversedMoviesList);

                List<String> serialsList = database.getSerialsMap().values().stream().
                        filter(s -> s.numberOfFavorites(database) > 0).
                        sorted(Comparator.comparingInt(s -> s.numberOfFavorites(database))).
                        map(Serial::getTitle).toList();
                List<String> reversedSerialsList = new ArrayList<>(serialsList);
                Collections.reverse(reversedSerialsList);

                int foundMovie = 0;
                String foundMovieTitle = null;
                for (String currentMovie : reversedMoviesList) {
                    if (!user.getHistory().containsKey(currentMovie)) {
                        foundMovieTitle = currentMovie;
                        foundMovie = 1;
                        break;
                    }
                }
                int foundSerial = 0;
                String foundSerialTitle = null;
                for (String currentSerial : reversedSerialsList) {
                    if (!user.getHistory().containsKey(currentSerial)) {
                        foundSerialTitle = currentSerial;
                        foundSerial = 1;
                        break;
                    }
                }
                if (foundSerial == 0 && foundMovie == 1)
                    output.displayFavoriteRecommendation(action.getActionId(), foundMovieTitle);
                else if (foundMovie == 0 && foundSerial == 1)
                    output.displayFavoriteRecommendation(action.getActionId(), foundSerialTitle);
                else if (foundMovie == 1 && foundSerial == 1) {
                    if (database.getSerialsMap().get(foundSerialTitle).numberOfFavorites(database) >
                            database.getMoviesMap().get(foundMovieTitle).numberOfFavorites(database))
                        output.displayFavoriteRecommendation(action.getActionId(), foundSerialTitle);
                    else
                        output.displayFavoriteRecommendation(action.getActionId(), foundMovieTitle);
                } else
                    output.displayErrorFavoriteRecommendation(action.getActionId());
            }
        } else if (action.getType().equals("search")) {
            User user = database.getUsersMap().get(action.getUsername());
            if (user.getSubscriptionType().equals("BASIC")) {
                output.displayErrorPopularRecommendation(action.getActionId());
            } else {
                List<Show> showList = new ArrayList<>();
                for (Movie currentMovie : database.getMoviesMap().values()) {
                    if (currentMovie.hasGenre(Utils.stringToGenre(action.getGenre())))
                        showList.add(currentMovie);
                }
                for (Serial currentSerial : database.getSerialsMap().values()) {
                    if (currentSerial.hasGenre(Utils.stringToGenre(action.getGenre())))
                        showList.add(currentSerial);
                }

                List<String> resultList = showList.stream().sorted(Comparator.comparing(Show::getTitle)).
                        sorted(Comparator.comparingDouble(Show::calculateShowGrade)).map(Show::getTitle).
                        toList();

                List<String> displayList = new ArrayList<>();
                for (String currentShow : resultList) {
                    if (!user.getHistory().containsKey(currentShow))
                        displayList.add(currentShow);
                }
                if (displayList.isEmpty())
                    output.displayErrorSearchRecommendation(action.getActionId());
                else
                    output.displaySearchRecommendation(action.getActionId(), displayList);
            }
        }
    }
}