package servlets.queryresponseservlet;

import com.google.gson.Gson;
import database.DatabaseQueryRunner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import requestmodel.MessageQueryRequestModel;
import responsemodels.QueryResponseMessage;
import sensormodels.activfit.ActivFitSensorData;
import utils.QueryUtils;

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
    logPostRequest(requestHeaderString);

    MessageQueryRequestModel queryMessage =
        gson.fromJson(requestHeaderString, MessageQueryRequestModel.class);

    Date userDate = QueryUtils.extractDateFromQuery(queryMessage.getQuery());
    if (userDate == null) {
      onDateNotParsed();
    }

    switch (QueryUtils.determineQueryType(queryMessage.getQuery())) {
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

  private void logPostRequest(String requestHeaderString) {
    System.out.printf(
        "%s POST request called with request\n%s", getServletName(), requestHeaderString);
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
  private void sendResponse(@Nonnull String queryResponseData) {
    QueryResponseMessage queryResponseMessage =
        new QueryResponseMessage.Builder().setResponseMessage(queryResponseData).build();
    try {
      response.getOutputStream().print(gson.toJson(queryResponseMessage));
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      onPostResponse();
    }
  }

  /** Called after response is sent. Template method. */
  protected void onPostResponse() {}

  public void onDisplayHeartRateEventSelected(Date date) {
    String queryResult =
        QueryUtils.getFormattedHeartRatesForTheDays(
            date, this.databaseQueryRunner.queryHeartRatesForDay(date));
    sendResponse(queryResult);
  }

  public void onDisplayTotalStepsInDayEventSelected(Date date) {
    int totalStepsInDay = this.databaseQueryRunner.queryForTotalStepsInDay(date);
    sendResponse(QueryUtils.getFormattedTotalStepsForTheDay(totalStepsInDay, date));
  }

  public void onDateNotParsed() {
    sendResponse("Incorrect date, enter in this format: MM/dd/YYYY");
  }

  public void onNoEventResolved() {
    sendResponse("Could not recognise the query");
  }
}
