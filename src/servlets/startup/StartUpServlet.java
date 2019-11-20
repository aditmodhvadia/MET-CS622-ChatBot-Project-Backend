package servlets.startup;

import database.MongoDBManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class StartUpServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        System.out.println("--------#####--------");
        System.out.println("        Server started      ");
        System.out.println("--------#####--------");
//        Execute work from here
//        testing libs
        MongoDBManager.init();
    }
}
