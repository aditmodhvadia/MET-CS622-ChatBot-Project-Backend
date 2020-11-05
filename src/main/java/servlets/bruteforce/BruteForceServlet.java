package servlets.bruteforce;

import database.FileCumulator;
import servlets.queryresponseservlet.QueryResponseServlet;

public class BruteForceServlet extends QueryResponseServlet {

  public BruteForceServlet() {
    super(FileCumulator.getInstance());
  }
}
