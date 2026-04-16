package connection;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DBInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("DBInitListener: Tomcat is starting. Checking database...");
        try {
            Config config = new Config();
            Connection con = config.getcon();
            if (con == null) {
                System.out.println("DBInitListener: Connection is null, skipping auto-import.");
                return;
            }

            // Quick check if the database is already imported
            boolean rs = con.getMetaData().getTables(null, null, "admin", null).next();
            if (rs) {
                System.out.println("DBInitListener: Database 'admin' table already exists! Skipping import.");
                con.close();
                return;
            }

            System.out.println("DBInitListener: Database is completely empty! Reading examshow.sql to inject tables...");
            InputStream is = sce.getServletContext().getResourceAsStream("/WEB-INF/examshow.sql");
            if (is == null) {
                System.err.println("DBInitListener: examshow.sql not found in /WEB-INF/");
                return;
            }

            Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name());
            scanner.useDelimiter(";");
            
            Statement stmt = con.createStatement();
            int count = 0;
            while (scanner.hasNext()) {
                String sql = scanner.next();
                if (sql.trim().length() > 0 && !sql.trim().startsWith("/*")) {
                    try {
                        stmt.execute(sql);
                        count++;
                    } catch (Exception e) {
                        System.err.println("Warning on executing statement: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("DBInitListener: Successfully ran " + count + " queries! Database is now deployed.");
            con.close();

        } catch (Exception e) {
            System.err.println("DBInitListener: Failed to run automatic SQL import.");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do
    }
}
