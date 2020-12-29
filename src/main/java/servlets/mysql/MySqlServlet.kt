package servlets.mysql

import database.MySqlManager
import servlets.queryresponseservlet.QueryResponseServlet

class MySqlServlet : QueryResponseServlet(MySqlManager.instance)