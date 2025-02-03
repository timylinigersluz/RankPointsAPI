package ch.ksrminecraft.RangAPI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PointsAPI {
    Connection connection;
    public PointsAPI(Connection conn) {
        connection = conn;
    }
    public int SQLgetInt(String command) {
        try (PreparedStatement ps = connection.prepareStatement(command)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void SQLUpdate(String command){
        try (PreparedStatement ps = connection.prepareStatement(command)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
