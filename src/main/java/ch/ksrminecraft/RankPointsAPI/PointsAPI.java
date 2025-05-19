package ch.ksrminecraft.RankPointsAPI;


import java.sql.Connection;
import java.util.UUID;

public class PointsAPI {
    Database db;
    Connection con;
    DatabaseAPI api;

    public PointsAPI(String url, String user, String pass) {
        this.db = new Database();
        db.connect(url, user, pass);
        this.con = db.getConnection();
        this.api = new DatabaseAPI(con);
    }

    /*
    Set the points from a specific Player to any given amount
    Not recommended for regular Use. Use addPoints instead
     */
    public void setPoints(UUID uuid, int points){
        api.SQLUpdate("Update points set points = " + points + " where UUID = ' " + uuid  + "'");
    }
    /*
    Add a number of Points to the total Score of a specified Player
    delta can be negative for points subtraction
     */
    public void addPoints(UUID uuid, int delta){
        int points = api.SQLgetInt("Select points from points where UUID = ' " + uuid + "'");
        int newPoints = points + delta;
        api.SQLUpdate("Update points set points = " + newPoints + " where UUID = ' " + uuid + "'");
    }
    /*
    Returns the current Points score from a given Player
     */
    public int getPoints(UUID uuid){
        int points = api.SQLgetInt("Select points from points where UUID = ' " + uuid + "'");
        return points;
    }
}
