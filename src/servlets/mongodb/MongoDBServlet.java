package servlets.mongodb;

import com.google.gson.Gson;
import responsemodels.QueryResponseMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MongoDBServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        QueryResponseMessage msg = new QueryResponseMessage();
        QueryResponseMessage.Data data = new QueryResponseMessage.Data("MongoDB called");
        msg.setData(data);
        Gson g = new Gson();
        resp.getOutputStream().print(g.toJson(msg));
    }
}
