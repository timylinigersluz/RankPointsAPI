package ch.ksrminecraft;

import java.sql.Connection;
import java.util.UUID;

public class RangAPI {
    Database db;
    Connection con;
    PointsAPI api;

    public RangAPI(String url, String user, String pass) {
        this.db = new Database();
        db.connect(url, user, pass);
        this.con = db.getConnection();
        this.api = new PointsAPI(con);
    }

    public void setPoints(UUID uuid, int points){
        api.SQLUpdate("Update points set points = " + points + " where UUID = ' " + uuid  + "'");
    }
    public void addPoints(UUID uuid, int delta){
        int points = api.SQLgetInt("Select points from points where UUID = ' " + uuid + "'");
        int newPoints = points + delta;
        api.SQLUpdate("Update points set points = " + newPoints + " where UUID = ' " + uuid + "'");
    }
    public int getPoints(UUID uuid){
        int points = api.SQLgetInt("Select points from points where UUID = ' " + uuid + "'");
        return points;
    }
}
