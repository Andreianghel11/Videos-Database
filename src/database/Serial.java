package database;

import entertainment.Season;
import fileio.SerialInputData;

import java.util.ArrayList;

/**
 * Clasa definește obiectele de tip serial.
 * Serialele sunt extensii ale clasei show.
 */
public final class Serial extends Show {
    private int numberOfSeasons;

    private ArrayList<Season> seasons;

    /**
     * Constructor specializat.
     */
    public Serial(final SerialInputData serial) {
        super(serial.getTitle(), serial.getYear(), serial.getCast(), serial.getGenres());
        this.seasons = serial.getSeasons();
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(final int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(final ArrayList<Season> seasons) {
        this.seasons = seasons;
    }

    /**
     * Calculează rating-ul unui serial
     * ținând cont de notele fiecărui sezon.
     */
    @Override
    public double calculateShowGrade() {
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

    /**
     * Calculează rating-ul unui sezon
     * ținând cont de toate rating-urile utilizatorilor.
     */
    public double calculateSeasonGrade(final Season season) {
        double rating = 0.0;
        if (season.getRatings().isEmpty()) {
            return 0.0;
        }
        for (Double currentRating : season.getRatings()) {
            rating += currentRating;
        }
        return rating / season.getRatings().size();
    }

    /**
     * Calculează durata unui serial
     * ținând cont de durata sezoanelor.
     */
    public int calculateDuration() {
        int duration = 0;
        for (Season currentSeason : seasons) {
            duration += currentSeason.getDuration();
        }
        return duration;
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
