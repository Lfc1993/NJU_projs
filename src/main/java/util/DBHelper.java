package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**API to access mysql DB
 * Created by lfc on 2016/3/22.
 */
public class DBHelper {
    private static final String url = "jdbc:mysql://127.0.0.1/wiki_test?characterEncoding=UTF-8";
    private static final String name = "com.mysql.jdbc.Driver";
    private static String user = "root";
    private static String password = "12345678";


    public static Connection getConn() throws Exception {
        Connection conn = null;

        Class.forName(name);//指定连接类型
        while (null == conn) {
            try {
                conn = DriverManager.getConnection(url, user, password);//获取连接
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }
        return conn;
    }


    public static void close(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
