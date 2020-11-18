package servlets.mysql;

import database.MySqlManager;
import servlets.queryresponseservlet.QueryResponseServlet;

public class MySqlServlet extends QueryResponseServlet {

  public MySqlServlet() {
    super(MySqlManager.getInstance());
  }
}
