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
      out.println("<head>");
      out.println("<title>Responses</title>");
      out.println("<link rel='stylesheet' type='text/css' href='style.css'>");
      out.println("</head>");
      out.println("<body>");

      try (
         // Step 1: Allocate a database 'Connection' object
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/clicker?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");   // For MySQL

         // Step 2: Allocate a 'Statement' object in the Connection
         Statement stmt = conn.createStatement();
      ) {
         // Steps 3-4
         String questionNo = request.getParameter("questionNo");
         String sqlStr = "SELECT choice, COUNT(*) AS count FROM responses WHERE questionNo = " + questionNo + " GROUP BY choice";

         // for debugging
         // out.println("<p>Your SQL statement is: " + sqlStr + "</p>"); // Echo for debugging

         ResultSet rset = stmt.executeQuery(sqlStr); // run the SQL statemenT

         int a=0, b=0, c=0, d=0;

         out.println("<ul>");
         while (rset.next()) {
            String choice = rset.getString("choice");
            int count = rset.getInt("count");
            // for debugging
            // out.println("<li>" + choice + ": " + count + " responses</li>");
            switch (choice.trim().toUpperCase()) {
               case "A": a = count; break;
               case "B": b = count; break;
               case "C": c = count; break;
               case "D": d = count; break;
            }
         }
         
         // for debugging
         // out.println(a);
         // out.println(b);
         // out.println(c);
         // out.println(d);
         // out.println("</ul>");

         String barColors = "['rgba(210,99,99)', 'rgba(210,99,99)', 'rgba(210,99,99)', 'rgba(210,99,99)']";
         switch (questionNo) {
            case "1":
               barColors = "['rgba(210,99,99)', 'rgba(148,210,99)', 'rgba(210,99,99)', 'rgba(210,99,99)']";
               break;
            case "2":
               barColors = "['rgba(210,99,99)', 'rgba(210,99,99)', 'rgba(210,99,99)', 'rgba(148,210,99)']";
               break;
            case "3":
               barColors = "['rgba(210,99,99)', 'rgba(148,210,99)', 'rgba(210,99,99)', 'rgba(210,99,99)']";
               break;
            case "4":
               barColors = "['rgba(210,99,99)', 'rgba(210,99,99)', 'rgba(148,210,99)', 'rgba(210,99,99)']";
               break;
            case "5":
               barColors = "['rgba(148,210,99)', 'rgba(210,99,99)', 'rgba(210,99,99)', 'rgba(210,99,99)']";
               break;
         }

         out.println("<div class='responses-container'>");
         out.println("<script src='https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.4/Chart.js'>");
         out.println("</script>");
         out.println("<canvas id='myChart' style='width:100%; height:400px; display:block; margin: 0 auto;'></canvas>");

         out.println("<script>");
         out.println("const xValues = ['A', 'B', 'C', 'D'];");
         out.println("const yValues = [" + a + ", " + b + ", " + c +", " + d + "];");
         out.println("const barColors = " + barColors + ";");

         out.println("new Chart('myChart', {");
         out.println("   type: 'bar',");
         out.println("   data: {");
         out.println("      labels: xValues,");
         out.println("      datasets: [{");
         out.println("         backgroundColor: barColors,");
         out.println("         data: yValues");
         out.println("      }]");
         out.println("   },");
         out.println("   options: {");
         out.println("      legend: {");
         out.println("        display: false");
         out.println("      },");
         out.println("      scales: {");
         out.println("         yAxes: [{");
         out.println("            ticks: {");
         out.println("               beginAtZero: true");
         out.println("            }");
         out.println("         }]");
         out.println("      },");
         out.println("      title: {");
         out.println("         display: true,");
         out.println("         text: 'Responses'");
         out.println("      }");
         out.println("   }");
         out.println("});");
         out.println("</script>");

         int intQuestionNo = Integer.parseInt(questionNo);
         int nextQuestionNo = intQuestionNo + 1;

         out.println("<a href='http://localhost:9999/clicker/question" + intQuestionNo + ".html'>");
         out.println("<button class='button'>Return to Question</button>");
         out.println("</a>");

         if (nextQuestionNo <= 5) {
            out.println("<a href='http://localhost:9999/clicker/question" + nextQuestionNo + ".html'>");
            out.println("<button class='button'>Next Question</button>");
            out.println("</a>");
         } else {
            out.println("<a href='http://localhost:9999/clicker/completionpage.html'>");
            out.println("<button class='button'>Complete Quiz</button>");
            out.println("</a>");
         }
         out.println("</div>");
         

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
