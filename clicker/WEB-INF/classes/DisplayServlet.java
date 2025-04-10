import java.io.*;
import java.sql.*;
import jakarta.servlet.*;            // Tomcat 10 (Jakarta EE 9)
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
//import javax.servlet.*;            // Tomcat 9 (Java EE 8 / Jakarta EE 8)
//import javax.servlet.http.*;
//import javax.servlet.annotation.*;

@WebServlet("/display")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class DisplayServlet extends HttpServlet {

   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      // Set the MIME type for the response message
      response.setContentType("text/html");
      // Get a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
      // Print an HTML page as the output of the query
      out.println("<!DOCTYPE html>");
      out.println("<html>");
      out.println("<head><title>Display Response</title></head>");
      out.println("<body>");

      try (
         // Step 1: Allocate a database 'Connection' object
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/clicker?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");   // For MySQL

         // Step 2: Allocate a 'Statement' object in the Connection
         Statement stmt = conn.createStatement();
      ) {
         
         // TO-DO: un-hardcode questions
         // TO-DO: bar chart showing results

         String sqlStr = "select choice from responses where questionNo = 8 order by choice asc";

         out.println("<h3>Here are the responses:</h3>");
         out.println("<p>Your SQL statement is: " + sqlStr + "</p>"); // Echo for debugging

         ResultSet rset = stmt.executeQuery(sqlStr); // run the SQL statement

         int count = 0;
         while(rset.next()) {
            out.println("<p>" + rset.getString("choice") + "</p>");
            count++;
         }
         out.println("<p>==== " + count + " records found =====</p>");

         // === Do NOT delete the following codes ===
      } catch(SQLException ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
 
      out.println("</body></html>");
      out.close();
   }
}