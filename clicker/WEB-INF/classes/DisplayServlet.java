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
         
         // TO-DO: bar chart showing results

         String questionNo = request.getParameter("questionNo");
         String sqlStr = "SELECT choice, COUNT(*) AS count FROM responses WHERE questionNo = " + questionNo + " GROUP BY choice";

         out.println("<h3>Here are the responses:</h3>");
         out.println("<p>Your SQL statement is: " + sqlStr + "</p>"); // Echo for debugging

         ResultSet rset = stmt.executeQuery(sqlStr); // run the SQL statement

         out.println("<h2>Statistics for Question " + questionNo + ":</h2>");
         out.println("<ul>");
         while (rset.next()) {
            String choice = rset.getString("choice");
            int count = rset.getInt("count");
            out.println("<li>" + choice + ": " + count + " responses</li>");
         }
         out.println("</ul>");

         /*
         int count = 0;
         while(rset.next()) {
            out.println("<p>" + rset.getString("choice") + "</p>");
            count++;
         }
         out.println("<p>==== " + count + " records found =====</p>");
         */

         int intQuestionNo = Integer.parseInt(questionNo);
         int nextQuestionNo = intQuestionNo + 1;

         if (nextQuestionNo <= 5) {
            out.println("<a href='http://localhost:9999/clicker/question" + nextQuestionNo + ".html'>");
            out.println("<button>Next Question</button>");
            out.println("</a>");
         } else {
            out.println("<a href='http://localhost:9999/clicker/completionpage.html'>");
            out.println("<button>Complete Quiz</button>");
            out.println("</a>");
         }

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
