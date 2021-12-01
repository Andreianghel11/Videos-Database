package database;

import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.List;

/**
 * Clasa definește obiectele de tip Output ce vor fi utilizate
 * pentru scrierea datelor de ieșire în fișierele de tip JSON.
 */
public final class Output {
    private final Writer fileWriter;
    private final JSONArray arrayResult;

    /**
     * Constructor standard.
     */
    public Output(final Writer fileWriter, final JSONArray arrayResult) {
        this.fileWriter = fileWriter;
        this.arrayResult = arrayResult;
    }

    /**
     * Afișarea unui mesaj.
     */
    public void displayFavoriteMessage(final int id, final String title) throws IOException {
        String message = "success -> " + title + " was added as favourite";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj de eroare.
     */
    public void displayDuplicateFavoriteMessage(final int id,
                                                final String title) throws IOException {
        String message = "error -> " + title + " is already in favourite list";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj.
     */
    public void displayViewMessage(final int id, final String title,
                                   final int views) throws IOException {
        String message = "success -> " + title + " was viewed with total views of "
                + views;
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj.
     */
    public void displayRatingMessage(final int id, final String title,
                                     final double rating, final String user) throws IOException {
        String message = "success -> " + title + " was rated with " + Double.toString(rating)
                + " by " + user;
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj de eroare.
     */
    public void displayErrorAlreadyRated(final int id, final String title) throws IOException {
        String message = "error -> " + title + " has been already rated";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj de eroare.
     */
    public void displayErrorNoTitle(final int id, final String title) throws IOException {
        String message = "error -> " + title + " is not seen";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj.
     */
    public void displayQueryResult(final int id, final List<String> list) throws IOException {
        String message = "Query result: " + list;
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj de eroare.
     */
    public void displayErrorBestRatedUnseenRecommendation(final int id) throws IOException {
        String message = "BestRatedUnseenRecommendation cannot be applied!";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj de eroare.
     */
    public void displayErrorFavoriteRecommendation(final int id) throws IOException {
        String message = "FavoriteRecommendation cannot be applied!";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj de eroare.
     */
    public void displayErrorPopularRecommendation(final int id) throws IOException {
        String message = "PopularRecommendation cannot be applied!";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj de eroare.
     */
    public void displayErrorSearchRecommendation(final int id) throws IOException {
        String message = "SearchRecommendation cannot be applied!";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj de eroare.
     */
    public void displayErrorStandardRecommendation(final int id) throws IOException {
        String message = "StandardRecommendation cannot be applied!";
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj.
     */
    public void displayStandardRecommendation(final int id, final String title) throws IOException {
        String message = "StandardRecommendation result: " + title;
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj.
     */
    public void displayBestRatedUnseenRecommendation(final int id,
                                                     final String title) throws IOException {
        String message = "BestRatedUnseenRecommendation result: " + title;
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj.
     */
    public void displayPopularRecommendation(final int id, final String title) throws IOException {
        String message = "PopularRecommendation result: " + title;
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj.
     */
    public void displayFavoriteRecommendation(final int id, final String title) throws IOException {
        String message = "FavoriteRecommendation result: " + title;
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

    /**
     * Afișarea unui mesaj.
     */
    public void displaySearchRecommendation(final int id,
                                            final List<String> titles) throws IOException {
        String message = "SearchRecommendation result: " + titles;
        arrayResult.add(fileWriter.writeFile(id, null, message));
    }

}
