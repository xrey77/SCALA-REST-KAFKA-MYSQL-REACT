// src/main/scala/Main.scala
import java.sql.{Connection, DriverManager, SQLException}

object Main extends App {
  val url = "jdbc:mysql://127.0.0:3306/scala_kafka"
  val username = "rey"
  val password = "rey"

  var connection: Connection = null

  try {

    Class.forName("com.mysql.cj.jdbc.Driver")
    connection = DriverManager.getConnection(url, username, password)
    
    if (connection != null && !connection.isClosed) {
      println("Successfully connected to the MySQL database!")
      
      // Perform database operations here (Statements, Queries, etc.)
    }
  } catch {
    case e: ClassNotFoundException => 
      println(s"MySQL JDBC Driver not found on the classpath: ${e.getMessage}")
    case e: SQLException => 
      println(s"Database connection failed: ${e.getMessage}")
  } finally {
    if (connection != null && !connection.isClosed) {
      connection.close()
      println("Database connection closed.")
    }
  }
}
