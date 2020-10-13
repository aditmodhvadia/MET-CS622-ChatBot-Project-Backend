package servlets.mysql;

import database.MySqlManager;
import servlets.queryresponseservlet.QueryResponseServlet;

public class MySQLServlet extends QueryResponseServlet {

    public MySQLServlet() {
        super(MySqlManager.getInstance());
    }
}
