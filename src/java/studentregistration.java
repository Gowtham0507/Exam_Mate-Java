/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import connection.*;
import java.security.MessageDigest;
import java.util.Base64;
/**
 *
 * @author ICT
 */
public class studentregistration extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String name=request.getParameter("sname");
        String rollno=request.getParameter("username");
        String passwd=request.getParameter("passwd");
        String institute=request.getParameter("institute");
        String sem="00";
        String email=request.getParameter("email");
        String number=request.getParameter("number");
        String dob="dd/mm/yyyy";
        String sex="gender";
        ResultSet rs;
           
        try
        {
            String code="12345";
            //password encription
            MessageDigest MD5=MessageDigest.getInstance("MD5");
            MD5.update(passwd.getBytes(),0,passwd.getBytes().length);
            byte[] hashvalue=MD5.digest();
            String newpasswd=Base64.getEncoder().encodeToString(hashvalue);
            Config c = new Config();
            Connection con = c.getcon();
            if (con == null) {
                throw new Exception("Database connection failed. Check DB variables on Render.");
            }
            Statement st = con.createStatement();
            String qry = "select count(*) as col from student where studentid ='"+rollno+"'";
            rs = st.executeQuery(qry);
            int check=0;
            if(rs.next())
            {
                check = Integer.parseInt(rs.getString("col"));
            }
            if(check==0)
            {
                String query="insert into student values('"+name+"','"+rollno+"','"+newpasswd+"','"+institute+"','"+sem+"','"+email+"','"+number+"','"+dob+"','"+sex+"','"+code+"')";
                st.executeUpdate(query);
                con.close();
                response.sendRedirect("index.jsp?RegisterStudent=True");
            }
            else
            {
                con.close();
                response.sendRedirect("index.jsp?existsStudent=True");
            }

        }
        catch(Exception e)
        {
            out.println("<h3>Registration Error: " + e.getMessage() + "</h3>");
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
