package servlets.lucene;

import database.LuceneManager;
import servlets.queryresponseservlet.QueryResponseServlet;

public class LuceneServlet extends QueryResponseServlet {

  public LuceneServlet() {
    super();
    changeDatabaseQueryRunner(LuceneManager.getInstance(this.getServletContext()));
  }
}
