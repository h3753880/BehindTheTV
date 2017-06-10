import java.util.ArrayList;

public class Show {

    private String[] genre;
    private ArrayList<Cast> people;
    private int duration;
    private double rating;
    private String synopsis;
    private String releaseDate;
    private String network;
    private String webChannel;
    private String title;
    private int numSeason;
    private double avgNumOfEp;
    private double imdbRating;
    private int imdbVotes;

    public double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public int getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(int imdbVotes) {
        this.imdbVotes = imdbVotes;
    }
    public int getNumSeason() {
        return numSeason;
    }

    public void setNumSeason(int numSeason) {
        this.numSeason = numSeason;
    }

    public double getAvgNumOfEp() {
        return avgNumOfEp;
    }

    public void setAvgNumOfEp(double avgNumOfEp) {
        this.avgNumOfEp = avgNumOfEp;
    }

    public ArrayList<Cast> getPeople() {
        return people;
    }

    public void setPeople(ArrayList<Cast> people) {
        this.people = people;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getGenre() {
        return genre;
    }

    public void setGenre(String[] genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getWebChannel() {
        return webChannel;
    }

    public void setWebChannel(String webChannel) {
        this.webChannel = webChannel;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

}
