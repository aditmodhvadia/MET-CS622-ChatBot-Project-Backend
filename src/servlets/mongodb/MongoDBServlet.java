package servlets.mongodb;

import com.google.gson.Gson;
import database.MongoDBManager;
import requestmodel.MessageQueryRequestModel;
import responsemodels.QueryResponseMessage;
import sensormodels.ActivFitSensorData;
import utils.QueryUtils;
import utils.WebAppConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.objects.Global.Infinity;

public class MongoDBServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("MongoDB POST request called");
        Gson g = new Gson();
        String requestHeaderString = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println(requestHeaderString);
        MessageQueryRequestModel queryMessage = g.fromJson(requestHeaderString, MessageQueryRequestModel.class);

        QueryResponseMessage msg = new QueryResponseMessage();

        QueryUtils.determineQueryType(queryMessage.getQuery(), new QueryUtils.OnQueryResolvedCallback() {
            @Override
            public void onDisplayRunningEventSelected(Date date) {
                ArrayList<ActivFitSensorData> queryResult = MongoDBManager.queryForRunningEvent(date);
                String queryResultString = getFormattedRunningResultData(queryResult);
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
                String queryResultString = getFormattedHeartRatesForTheDays(date, MongoDBManager.queryHeartRatesForDay());
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
                int queryResult = MongoDBManager.queryForTotalStepsInDay(date);
                String queryResultString = getFormattedTotalStepsForTheDay(queryResult, date);
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

//        QueryResponseMessage.Data data = new QueryResponseMessage.Data(queryMessage.getQuery());
//        msg.setData(data);
//        resp.getOutputStream().print(g.toJson(msg));
    }

    /**
     * Use to print the Query result data for running activity on the given Date
     *
     * @param queryResult the given Result from the Query
     */
    private static String getFormattedRunningResultData(ArrayList<ActivFitSensorData> queryResult) {
        if (queryResult.isEmpty()) {
            System.out.println("No, there is no running activity.");
            return "No, there is no running activity.";
        } else {
            StringBuilder builder = new StringBuilder();
            for (ActivFitSensorData data : queryResult) {
                builder.append("Yes, you ran from ")
                        .append(data.getTimestamp().getStartTime())
                        .append(" to ")
                        .append(data.getTimestamp().getEndTime());
                System.out.println("Yes, you ran from " + data.getTimestamp().getStartTime() + " to " + data.getTimestamp().getEndTime());
            }
            return builder.toString();
        }
    }

    /**
     * Use to print the Query result from counting the total steps of the day
     *
     * @param stepCount given step count
     * @param userDate  given Date of the step count
     */
    private static String getFormattedTotalStepsForTheDay(int stepCount, Date userDate) {
        if (stepCount == (int) -Infinity) {
            return "No steps record found for the day";
        } else {
            return "You walked " + stepCount + " steps on " + WebAppConstants.inputDateFormat.format(userDate);
        }
    }

    /**
     * Call to get formatted output for HeartRates for the days
     *
     * @param date
     * @param heartRates
     */
    private static String getFormattedHeartRatesForTheDays(Date date, HashMap<String, Integer> heartRates) {
        StringBuilder builder = new StringBuilder();
        if (heartRates.size() == 0) {
            builder.append("No data found in MongoDB or some error occurred.");
        } else {
            String formattedDate = WebAppConstants.inputDateFormat.format(date);
            if (heartRates.containsKey(formattedDate)) {
                builder.append("You received ")
                        .append(heartRates.get(formattedDate))
                        .append(" HeartRate notifications on ")
                        .append(formattedDate);
            } else {
                builder.append("No data found in MongoDB or some error occurred.");
            }
            /*for (String date :
                    heartRates.keySet()) {
                int hearRateCount = heartRates.get(date);
                builder.append("You received ")
                        .append(hearRateCount)
                        .append(" HeartRate notifications on ")
                        .append(date);
            }*/
        }
        return builder.toString();
    }
}
