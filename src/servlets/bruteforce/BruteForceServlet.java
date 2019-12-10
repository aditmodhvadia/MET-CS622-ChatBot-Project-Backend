package servlets.bruteforce;

import com.google.gson.Gson;
import database.MongoDBManager;
import requestmodel.MessageQueryRequestModel;
import responsemodels.QueryResponseMessage;
import sensormodels.ActivFitSensorData;
import utils.FileCumulator;
import utils.QueryUtils;

import javax.management.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class BruteForceServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Brute Force POST request called");
        Gson g = new Gson();
        String requestHeaderString = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println(requestHeaderString);
        MessageQueryRequestModel queryMessage = g.fromJson(requestHeaderString, MessageQueryRequestModel.class);

        QueryResponseMessage msg = new QueryResponseMessage();

        QueryUtils.determineQueryType(queryMessage.getQuery(), new QueryUtils.OnQueryResolvedCallback() {
            @Override
            public void onDisplayRunningEventSelected(Date date) {
                ArrayList<ActivFitSensorData> queryResult = FileCumulator.queryForRunningEvent(date);
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
                String queryResultString = QueryUtils.getFormattedHeartRatesForTheDays(date, FileCumulator.queryHeartRatesForDay(date));
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
                int queryResult = FileCumulator.queryForTotalStepsInDay(date);
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
