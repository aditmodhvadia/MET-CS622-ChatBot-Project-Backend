package servlets.mongodb;

import database.MongoDbManager;
import servlets.queryresponseservlet.QueryResponseServlet;

public class MongoDbServlet extends QueryResponseServlet {

  public MongoDbServlet() {
    super(MongoDbManager.getInstance());
  }

  @Override
  protected void onPostResponse() {
    System.out.println("MongoDB ran a query.");
  }
}
