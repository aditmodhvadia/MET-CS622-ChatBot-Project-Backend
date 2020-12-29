package servlets.mongodb

import database.MongoDbManager.Companion.instance
import servlets.queryresponseservlet.QueryResponseServlet
import database.MongoDbManager

class MongoDbServlet : QueryResponseServlet(instance) {
    override fun onPostResponse() {
        println("MongoDB ran a query.")
    }
}