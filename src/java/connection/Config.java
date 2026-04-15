/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package connection;

/**
 *
 * @author aqfaridi
 */

import java.sql.*;
public class Config
{
    Connection con=null;
    String url = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/examshow";
    String user = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    String pass = System.getenv("DB_PASS") != null ? System.getenv("DB_PASS") : "root";

    public Connection getcon()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url,user,pass);

        }
        catch(Exception e)
        {
          e.printStackTrace();
        }

        return con;
    }

}
