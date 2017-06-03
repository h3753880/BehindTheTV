import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Howard on 2017/5/30.
 */
public class Normal {
    private ArrayList<Show> shows;
    private double[][] mat;
    private ArrayList<Double> rating;
    private HashMap<Integer, String> map;
    private HashMap<String, Integer> genMap;
    private ArrayList<String> genres;
    private ArrayList<String> channels;

    public Normal(ArrayList<Show> shows) {
        this.shows = shows;

        rating = new ArrayList<Double>();
        map = new HashMap<>();
        genMap = new HashMap<>();
        genres = Tool.readFileByLine("genre.txt");
        channels = Tool.readFileByLine("net.txt");
        mat = new double[shows.size()][13+genres.size()+channels.size()];
    }

    public void normFeatures() {
        DecimalFormat df = new DecimalFormat("##.000");
        ArrayList<Double> prods = new ArrayList<>();
        ArrayList<Double> creats = new ArrayList<>();
        ArrayList<Double> novels = new ArrayList<>();
        ArrayList<Double> duras = new ArrayList<>();
        ArrayList<Double> noseason = new ArrayList<>();
        ArrayList<Double> avgEp = new ArrayList<>();
        ArrayList<Double> act1 = new ArrayList<>();
        ArrayList<Double> act2 = new ArrayList<>();
        ArrayList<Double> act3 = new ArrayList<>();

        // create rating array
        setRating();

        createMapping();

        // features
        createGenre();
        createPeople();
        createChannel();
        createRelease();

        // duration numSeason avgNumOfEp
        for(int i=0; i<shows.size(); i++) {
            mat[i][genMap.get("duration")] = shows.get(i).getDuration();
            mat[i][genMap.get("numSeason")] = shows.get(i).getNumSeason();
            mat[i][genMap.get("avgNumOfEp")] = shows.get(i).getAvgNumOfEp();
        }

        // normalize
        for(int i=0; i<shows.size(); i++) {
            prods.add(mat[i][genMap.get("Executive Producer")]);
            creats.add(mat[i][genMap.get("Creator")]);
            novels.add(mat[i][genMap.get("Based on the Novel Of")]);
            duras.add(mat[i][genMap.get("duration")]);
            noseason.add(mat[i][genMap.get("numSeason")]);
            avgEp.add(mat[i][genMap.get("avgNumOfEp")]);
            act1.add(mat[i][genMap.get("Actor1")]);
            act2.add(mat[i][genMap.get("Actor2")]);
            act3.add(mat[i][genMap.get("Actor3")]);
        }

        prods = Tool.scaling(prods);
        creats = Tool.scaling(creats);
        novels = Tool.scaling(novels);
        duras = Tool.scaling(duras);
        noseason = Tool.scaling(noseason);
        avgEp = Tool.scaling(avgEp);
        act1 = Tool.scaling(act1);
        act2 = Tool.scaling(act2);
        act3 = Tool.scaling(act3);

        for(int i=0; i<shows.size(); i++) {
            mat[i][genMap.get("Executive Producer")] = Double.parseDouble(df.format(prods.get(i)));
            mat[i][genMap.get("Creator")] = Double.parseDouble(df.format(creats.get(i)));
            mat[i][genMap.get("Based on the Novel Of")] = Double.parseDouble(df.format(novels.get(i)));
            mat[i][genMap.get("duration")] = Double.parseDouble(df.format(duras.get(i)));
            mat[i][genMap.get("numSeason")] = Double.parseDouble(df.format(noseason.get(i)));
            mat[i][genMap.get("avgNumOfEp")] = Double.parseDouble(df.format(avgEp.get(i)));
            mat[i][genMap.get("Actor1")] = Double.parseDouble(df.format(act1.get(i)));
            mat[i][genMap.get("Actor2")] = Double.parseDouble(df.format(act2.get(i)));
            mat[i][genMap.get("Actor3")] = Double.parseDouble(df.format(act3.get(i)));

        }
    }

    public void createRelease() {
        int season = 0;

        for(int i=0; i<shows.size(); i++) {
            season = Integer.parseInt(shows.get(i).getReleaseDate().substring(5,7));

            if(season >= 3 && season <= 5) {
                mat[i][genMap.get("springRel")] = 1;
            } else if(season >=6 && season <= 8) {
                mat[i][genMap.get("summerRel")] = 1;
            } else if(season >=9 && season <=11) {
                mat[i][genMap.get("fallRel")] = 1;
            } else {
                mat[i][genMap.get("winterRel")] = 1;
            }
        }
    }

    public void createChannel() {
        for(int i=0; i<shows.size(); i++) {
            if(shows.get(i).getWebChannel() != null && !shows.get(i).getWebChannel().equals("")) {
                mat[i][genMap.get(shows.get(i).getWebChannel())] = 1;
            } else {
                mat[i][genMap.get(shows.get(i).getNetwork())] = 1;
            }
        }
    }

    public void createPeople() {
        ArrayList<Cast> casts = null;
        ArrayList<Cast> actors = new ArrayList<>();
        double producer = 0;
        int countPord = 0;
        double creator = 0;
        int countCrea = 0;
        double novel = 0;
        int countNov = 0;

        for(int i=0; i<shows.size(); i++) {
            casts = shows.get(i).getPeople();

            for(Cast c: casts) {
                switch (c.getType()) {
                    case "Actor":
                        actors.add(c);
                        break;
                    case "Executive Producer":
                        countPord++;
                        producer += c.getFollowers();
                        break;
                    case "Creator":
                        countCrea++;
                        creator += c.getFollowers();
                        break;
                    case "Based on the Novel Of":
                        countNov++;
                        novel += c.getFollowers();
                        break;
                }
            }

            // for producer
            if(countPord != 0)
                mat[i][genMap.get("Executive Producer")] = producer/countPord;

            // for creator
            if(countCrea != 0)
                mat[i][genMap.get("Creator")] = creator/countCrea;

            if(countNov != 0)
                mat[i][genMap.get("Based on the Novel Of")] = novel/countNov;

            // sort followers
            Collections.sort(actors, (c1, c2) -> c2.getFollowers() - c1.getFollowers());

            // for actors 1 2 3
            for(int count = 1; count<=3; count ++) {
                mat[i][genMap.get("Actor" + count)] = actors.get(count - 1).getFollowers();
            }


            // test
            /*for(Cast cc: actors)
                System.out.println(cc.getFollowers());
            System.out.println("test");*/
        }
    }


    public void createGenre() {
        for(int i=0; i<shows.size(); i++) {
            String[] gens = shows.get(i).getGenre();

            //row col
            for(String gen: gens) {
                mat[i][genMap.get(gen)] = 1;
            }
        }
    }

    public void createMapping() {
        //genres 16
        for(String gen: genres) {
            map.put(map.size(), gen);
            genMap.put(gen, genMap.size());
        }

        //13
        map.put(map.size(), "Executive Producer");
        genMap.put("Executive Producer", genMap.size());
        map.put(map.size(), "Creator");
        genMap.put("Creator", genMap.size());
        map.put(map.size(), "Based on the Novel Of");
        genMap.put("Based on the Novel Of", genMap.size());

        map.put(map.size(), "Actor1");//Actor
        genMap.put("Actor1", genMap.size());
        map.put(map.size(), "Actor2");
        genMap.put("Actor2", genMap.size());
        map.put(map.size(), "Actor3");
        genMap.put("Actor3", genMap.size());

        map.put(map.size(), "duration");
        genMap.put("duration", genMap.size());

        // 17 channel
        for(String chan: channels) {
            map.put(map.size(), chan);//network webChannel
            genMap.put(chan, genMap.size());
        }

        map.put(map.size(), "numSeason");
        genMap.put("numSeason", genMap.size());
        map.put(map.size(), "avgNumOfEp");
        genMap.put("avgNumOfEp", genMap.size());

        map.put(map.size(), "springRel");//releaseDate
        genMap.put("springRel", genMap.size());
        map.put(map.size(), "summerRel");
        genMap.put("summerRel", genMap.size());
        map.put(map.size(), "fallRel");
        genMap.put("fallRel", genMap.size());
        map.put(map.size(), "winterRel");
        genMap.put("winterRel", genMap.size());
    }

    public void setRating() {
        for(Show item: this.shows) {
            rating.add(item.getRating());
        }
    }

    /**
     * get attribute name by matrix col index
     * @return hashMap<Integer, String>
     */
    public HashMap<Integer, String> getMap() {
        return map;
    }

    /**
     * get matrix col index by attribute name
     * @return hashMap<String, Integer>
     */
    public HashMap<String, Integer> getGenMap() {
        return genMap;
    }

    /**
     * get matrix
     * @return 2D matrix array (double)
     */
    public double[][] getMat() {
        return mat;
    }

    /**
     * get rating
     * @return ArrayList<Double> with rating
     */
    public ArrayList<Double> getRating() {
        return rating;
    }

    public void printMat() {
        System.out.println("row = " + mat.length);
        System.out.println("col = " + mat[0].length);
        for(int i=0; i<mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++)
                System.out.print(mat[i][j] + " ");
            System.out.println();
        }
    }

    /**
     * get all tv show titles
     * @return titles
     */
    public ArrayList<String> getTitles() {
        ArrayList<String> titles = new ArrayList<>();

        for(Show s: this.shows) {
            titles.add(s.getTitle());
        }

        return titles;
    }
}
