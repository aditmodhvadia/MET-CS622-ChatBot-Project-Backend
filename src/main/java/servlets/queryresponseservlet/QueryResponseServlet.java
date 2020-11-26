package servlets.queryresponseservlet;

import com.google.gson.Gson;
import database.DatabaseQueryRunner;
import requestmodel.MessageQueryRequestModel;
import responsemodels.QueryResponseMessage;
import sensormodels.ActivFitSensorData;
import utils.QueryUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public abstract class QueryResponseServlet extends HttpServlet {

  private HttpServletResponse response;
  private final Gson gson = new Gson();
  private DatabaseQueryRunner databaseQueryRunner;

  public QueryResponseServlet(DatabaseQueryRunner databaseQueryRunner) {
    this.databaseQueryRunner = databaseQueryRunner;
  }

  public QueryResponseServlet() {}

  /**
   * Change the database query runner at runtime
   *
   * @param dbManager database query runner
   */
  protected void changeDatabaseQueryRunner(DatabaseQueryRunner dbManager) {
    this.databaseQueryRunner = dbManager;
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    this.response = resp;
    String requestHeaderString =
            req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    System.out.println(getServletName() + " POST request called with request ");
    System.out.println(requestHeaderString);
    MessageQueryRequestModel queryMessage =
        gson.fromJson(requestHeaderString, MessageQueryRequestModel.class);

    Date userDate = QueryUtils.extractDateFromQuery(queryMessage.getQuery());

    if (userDate == null) {
      onDateNotParsed();
    }

    QueryUtils.QueryType queryType = QueryUtils.determineQueryType(queryMessage.getQuery());
    switch (queryType) {
      case RUNNING:
        onDisplayRunningEventSelected(userDate);
        break;
      case HEART_RATE:
        onDisplayHeartRateEventSelected(userDate);
        break;
      case STEP_COUNT:
        onDisplayTotalStepsInDayEventSelected(userDate);
        break;
      case UNKNOWN:
        onNoEventResolved();
        break;
    }
  }

  public void onDisplayRunningEventSelected(Date date) {
    ArrayList<ActivFitSensorData> queryResult = this.databaseQueryRunner.queryForRunningEvent(date);
    String queryResultString = QueryUtils.getFormattedRunningResultData(queryResult);
    sendResponse(queryResultString);
  }

  /**
   * Call to send back the response with given query response data.
   *
   * @param queryResponseData given query response data
   */
  private void sendResponse(String queryResponseData) {
    QueryResponseMessage msg = new QueryResponseMessage();
    QueryResponseMessage.Data data = new QueryResponseMessage.Data(queryResponseData);
    msg.setData(data);
    try {
      response.getOutputStream().print(gson.toJson(msg));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void onDisplayHeartRateEventSelected(Date date) {
    String queryResultString =
            QueryUtils.getFormattedHeartRatesForTheDays(
                    date, this.databaseQueryRunner.queryHeartRatesForDay(date));
    sendResponse(queryResultString);
  }

  public void onDisplayTotalStepsInDayEventSelected(Date date) {
    int queryResult = this.databaseQueryRunner.queryForTotalStepsInDay(date);
    String queryResultString = QueryUtils.getFormattedTotalStepsForTheDay(queryResult, date);
    sendResponse(queryResultString);
  }

  public void onDateNotParsed() {
    sendResponse("Incorrect date, enter in this format: MM/dd/YYYY");
  }

  public void onNoEventResolved() {
    sendResponse("Could not recognise the query");
  }
}
