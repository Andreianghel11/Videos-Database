package database;

import entertainment.Season;
import fileio.SerialInputData;

import java.util.ArrayList;

/* Clasa Serial implementata de mine. */
public class Serial extends Show{
    private int numberOfSeasons;

    private ArrayList<Season> seasons;

    public Serial(String title, int year, ArrayList<String> cast, ArrayList<String> genres, int numberOfSeasons, ArrayList<Season> seasons) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }

    public Serial(SerialInputData serial) {
        super(serial.getTitle(), serial.getYear(), serial.getCast(), serial.getGenres());
        this.seasons = serial.getSeasons();
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(ArrayList<Season> seasons) {
        this.seasons = seasons;
    }

    /*Calculeaza rating-ul unui serial, tinand cont de toate notele acordate de catre useri sezoanelor.
     Daca cel putin un sezon are rating, restul sezoanelor au nota 0.
     */
    public double calculateSerialGrade() {
        int gradeFound = 0;
        double rating = 0.0;
        for (Season currentSeason : seasons) {
            rating += calculateSeasonGrade(currentSeason);
            gradeFound = 1;
        }

        if (gradeFound == 0) {
            return 0.0;
        } else {
            return rating / seasons.size();
        }
    }

    /* Calculeaza rating-ul unui sezon, tinand cont de toate notele acordate de catre useri.*/
    public double calculateSeasonGrade(Season season) {
        double rating = 0.0;
        if (season.getRatings().isEmpty()) {
            return 0.0;
        }
        for (Double currentRating : season.getRatings()) {
            rating += currentRating;
        }
        return rating / season.getRatings().size();
    }

    @Override
    public String toString() {
        return "SerialInputData{" + " title= "
                + super.getTitle() + " " + " year= "
                + super.getYear() + " cast {"
                + super.getCast() + " }\n" + " genres {"
                + super.getGenres() + " }\n "
                + " numberSeason= " + numberOfSeasons
                + ", seasons=" + seasons + "\n\n" + '}';
    }
}
