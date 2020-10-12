package servlets.lucene;

import database.LuceneManager;
import sensormodels.ActivFitSensorData;
import servlets.queryresponseservlet.QueryResponseServlet;

import java.util.ArrayList;
import java.util.Date;

public class LuceneServlet extends QueryResponseServlet {
    private final LuceneManager luceneManager;

    public LuceneServlet() {
        this.luceneManager = LuceneManager.getInstance(getServletContext());
    }

    @Override
    public ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
        return this.luceneManager.queryForRunningEvent(userDate);
    }

    @Override
    public int queryHeartRatesForDay(Date date) {
        return this.luceneManager.queryHeartRatesForDay(date);
    }

    @Override
    public int queryForTotalStepsInDay(Date userDate) {
        return this.luceneManager.queryForTotalStepsInDay(userDate);
    }
}
