
public class Cast {
    private String type = "Actor";
    private String name = "";
    private String character = "";
    private int followers = 0;
    private int positive_review = 0;
    private int negative_review = 0;

    public int getPositive_review() {
        return positive_review;
    }

    public void setPositive_review(int positive_review) {
        this.positive_review = positive_review;
    }

    public int getNegative_review() {
        return negative_review;
    }

    public void setNegative_review(int negative_review) {
        this.negative_review = negative_review;
    }



    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }
}
