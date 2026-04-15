/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.PreparedStatement;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import connection.*;
import java.security.MessageDigest;
import java.util.Base64;

/**
 *
 * @author SAURABH
 */
public class studentlogin extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String uname=request.getParameter("username");
        String passwd=request.getParameter("passwd");
        String email=request.getParameter("email");
        ResultSet rs=null;

        try
        {
            HttpSession session = request.getSession(true);
            MessageDigest MD5=MessageDigest.getInstance("MD5");
            MD5.update(passwd.getBytes(),0,passwd.getBytes().length);
            byte[] hashvalue=MD5.digest();
            String newpasswd=Base64.getEncoder().encodeToString(hashvalue);
            
            Config c = new Config();
            Connection con = c.getcon();
            if (con == null) {
                throw new Exception("Database connection failed. Check DB variables.");
            }
            Statement st = con.createStatement();
        
            String selectStatement = "SELECT * FROM student WHERE studentID = ? ";
            PreparedStatement prepStmt = (PreparedStatement) con.prepareStatement(selectStatement);
            prepStmt.setString(1, uname);
            rs = prepStmt.executeQuery();
            if(rs.next())
            {
                if(newpasswd.equals(rs.getString("password")))
                {
                    session.setAttribute("username",rs.getString("studentname"));
                    session.setAttribute("studentid",rs.getString("studentID"));
                    session.setAttribute("which", "student");
                    session.setAttribute("q_id", "1");
                    con.close();
                    response.sendRedirect("studentprofile.jsp");                    
                }
                else
                {
                    con.close();
                    response.sendRedirect("index.jsp?RetryStudent=True");
                }
            }
            else{
                con.close();
               response.sendRedirect("index.jsp?RetryStudent=True");
            }

        }
        catch(Exception e)
        {
            out.println("<h3>Login Error: " + e.getMessage() + "</h3>");
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
