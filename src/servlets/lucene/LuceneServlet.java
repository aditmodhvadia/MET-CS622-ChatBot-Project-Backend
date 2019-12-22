package servlets.lucene;

import lucene.LuceneManager;
import sensormodels.ActivFitSensorData;
import servlets.queryresponseservlet.QueryResponseServlet;

import java.util.ArrayList;
import java.util.Date;

public class LuceneServlet extends QueryResponseServlet {

    @Override
    public ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
        return LuceneManager.queryForRunningEvent(userDate);
    }

    @Override
    public int queryHeartRatesForDay(Date date) {
        return LuceneManager.queryHeartRatesForDay(date);
    }

    @Override
    public int queryForTotalStepsInDay(Date userDate) {
        return LuceneManager.queryForTotalStepsInDay(userDate);
    }
}
