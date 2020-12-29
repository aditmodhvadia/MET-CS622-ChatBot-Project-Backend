package servlets.queryresponseservlet

import com.google.gson.Gson
import database.DatabaseQueryRunner
import requestmodel.MessageQueryRequestModel
import responsemodels.QueryResponseMessage.QueryResponseMessageBuilder
import utils.QueryUtils
import utils.QueryUtils.determineQueryType
import utils.QueryUtils.extractDateFromQuery
import utils.QueryUtils.getFormattedHeartRatesForTheDays
import utils.QueryUtils.getFormattedRunningResultData
import utils.QueryUtils.getFormattedTotalStepsForTheDay
import java.io.IOException
import java.util.*
import java.util.stream.Collectors
import javax.annotation.Nonnull
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class QueryResponseServlet : HttpServlet {
    private val gson = Gson()
    private var response: HttpServletResponse? = null
    private var databaseQueryRunner: DatabaseQueryRunner? = null

    constructor(databaseQueryRunner: DatabaseQueryRunner?) {
        this.databaseQueryRunner = databaseQueryRunner
    }

    constructor()

    /**
     * Change the database query runner at runtime
     *
     * @param dbManager database query runner
     */
    protected fun changeDatabaseQueryRunner(dbManager: DatabaseQueryRunner?) {
        databaseQueryRunner = dbManager
    }

    @Throws(ServletException::class, IOException::class)
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        response = resp
        val requestHeaderString = req.reader.lines().collect(Collectors.joining(System.lineSeparator()))
        logPostRequest(requestHeaderString)
        val queryMessage = gson.fromJson(requestHeaderString, MessageQueryRequestModel::class.java)
        val userDate = extractDateFromQuery(queryMessage.query)
        if (userDate == null) {
            onDateNotParsed()
        }
        handleQuery(queryMessage, userDate)
    }

    private fun handleQuery(queryMessage: MessageQueryRequestModel, userDate: Date?) {
        when (determineQueryType(queryMessage.query)) {
            QueryUtils.QueryType.RUNNING -> onDisplayRunningEventSelected(userDate)
            QueryUtils.QueryType.HEART_RATE -> onDisplayHeartRateEventSelected(userDate)
            QueryUtils.QueryType.STEP_COUNT -> onDisplayTotalStepsInDayEventSelected(userDate)
            QueryUtils.QueryType.UNKNOWN -> onNoEventResolved()
        }
    }

    private fun logPostRequest(requestHeaderString: String) {
        System.out.printf(
            "%s POST request called with request\n%s", servletName, requestHeaderString
        )
    }

    private fun onDisplayRunningEventSelected(date: Date?) {
        val queryResult = databaseQueryRunner!!.queryForRunningEvent(date)
        val queryResultString = getFormattedRunningResultData(queryResult)
        sendResponse(queryResultString)
    }

    /**
     * Call to send back the response with given query response data.
     *
     * @param queryResponseData given query response data
     */
    private fun sendResponse(@Nonnull queryResponseData: String) {
        val queryResponseMessage = QueryResponseMessageBuilder()
            .setResponseMessage(queryResponseData)
            .build()
        try {
            response!!.outputStream.print(gson.toJson(queryResponseMessage))
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            onPostResponse()
        }
    }

    /** Called after response is sent. Template method.  */
    protected open fun onPostResponse() {}
    private fun onDisplayHeartRateEventSelected(date: Date?) {
        val queryResult = getFormattedHeartRatesForTheDays(
            date, databaseQueryRunner!!.queryHeartRatesForDay(date)
        )
        sendResponse(queryResult)
    }

    private fun onDisplayTotalStepsInDayEventSelected(date: Date?) {
        val totalStepsInDay = databaseQueryRunner!!.queryForTotalStepsInDay(date)
        sendResponse(getFormattedTotalStepsForTheDay(totalStepsInDay, date))
    }

    private fun onDateNotParsed() {
        sendResponse("Incorrect date, enter in this format: MM/dd/YYYY")
    }

    private fun onNoEventResolved() {
        sendResponse("Could not recognise the query")
    }
}