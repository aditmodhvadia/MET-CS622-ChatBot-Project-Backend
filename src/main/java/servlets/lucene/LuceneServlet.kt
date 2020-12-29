package servlets.lucene

import database.LuceneManager.Companion.getInstance
import servlets.queryresponseservlet.QueryResponseServlet

class LuceneServlet : QueryResponseServlet() {
    init {
        changeDatabaseQueryRunner(getInstance(this.servletContext))
    }
}