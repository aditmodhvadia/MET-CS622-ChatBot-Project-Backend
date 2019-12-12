package servlets.mysql;

import com.google.gson.Gson;
import database.MongoDBManager;
import database.MySqlManager;
import requestmodel.MessageQueryRequestModel;
import responsemodels.QueryResponseMessage;
import sensormodels.ActivFitSensorData;
import sensormodels.HeartRateSensorData;
import utils.QueryUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class MySQLServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("MySQL POST request called");
        Gson g = new Gson();
        String requestHeaderString = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println(requestHeaderString);
        MessageQueryRequestModel queryMessage = g.fromJson(requestHeaderString, MessageQueryRequestModel.class);

        QueryResponseMessage msg = new QueryResponseMessage();

        QueryUtils.determineQueryType(queryMessage.getQuery(), new QueryUtils.OnQueryResolvedCallback() {
            @Override
            public void onDisplayRunningEventSelected(Date date) {
                ArrayList<ActivFitSensorData> queryResult = MySqlManager.queryForRunningEvent(date);
                String queryResultString = QueryUtils.getFormattedRunningResultData(queryResult);
                QueryResponseMessage.Data data = new QueryResponseMessage.Data(queryResultString);
                msg.setData(data);
                try {
                    resp.getOutputStream().print(g.toJson(msg));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisplayHeartRateEventSelected(Date date) {
//                ArrayList<HeartRateSensorData> queryResult = MySqlManager.queryHeartRatesForDay(date);
                String queryResultString = QueryUtils.getFormattedHeartRatesForTheDays(date, MySqlManager.queryHeartRatesForDay(date));
                QueryResponseMessage.Data data = new QueryResponseMessage.Data(queryResultString);
                msg.setData(data);
                try {
                    resp.getOutputStream().print(g.toJson(msg));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisplayTotalStepsInDayEventSelected(Date date) {
                int queryResult = MySqlManager.queryForTotalStepsInDay(date);
                String queryResultString = QueryUtils.getFormattedTotalStepsForTheDay(queryResult, date);
                QueryResponseMessage.Data data = new QueryResponseMessage.Data(queryResultString);
                msg.setData(data);
                try {
                    resp.getOutputStream().print(g.toJson(msg));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDateNotParsed() {
                QueryResponseMessage.Data data = new QueryResponseMessage.Data("Incorrect date, enter in this format: MM/dd/YYYY");
                msg.setData(data);
                try {
                    resp.getOutputStream().print(g.toJson(msg));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNoEventResolved() {
                QueryResponseMessage.Data data = new QueryResponseMessage.Data("Could not recognise the query");
                msg.setData(data);
                try {
                    resp.getOutputStream().print(g.toJson(msg));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
