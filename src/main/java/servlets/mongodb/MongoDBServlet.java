package servlets.mongodb;

import database.MongoDBManager;
import servlets.queryresponseservlet.QueryResponseServlet;

public class MongoDBServlet extends QueryResponseServlet {

    public MongoDBServlet() {
        super(MongoDBManager.getInstance());
    }
}
