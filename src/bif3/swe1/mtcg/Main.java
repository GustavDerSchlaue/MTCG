package bif3.swe1.mtcg;

import bif3.swe1.mtcg.classes.Store;
import bif3.swe1.mtcg.classes.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class Main {
    private static ArrayList<User> userList;

    public static Connection connection() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/mtcg";
            Properties properties = new Properties();
            properties.setProperty("user", "postgres");
            properties.setProperty("password", "");
            properties.setProperty("currentSchema", "mtcg");
            c = DriverManager.getConnection(url, properties);
            c.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        //System.out.println("Opened database successfully");
        return c;
    }

    public static void initializeAllUsers() throws SQLException {
        userList = new ArrayList<>();
        Connection c = connection();
        PreparedStatement ps = c.prepareStatement("SELECT id, username, password FROM public.user ORDER BY id;");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            userList.add(new User(rs.getInt("id"),rs.getString("username"),rs.getString("password")));
        }
        rs.close();
        ps.close();
        c.close();

        for(User u: userList) {
            u.updateData();
        }
        System.out.println("Initialization successful");
    }

    public static ArrayList<User> getUserList() { return userList; }

    public void Main() throws SQLException {
        initializeAllUsers();
        Store.initializeStore();
    }

}
