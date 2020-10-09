package servlets.bruteforce;

import sensormodels.ActivFitSensorData;
import servlets.queryresponseservlet.QueryResponseServlet;
import database.FileCumulator;

import java.util.ArrayList;
import java.util.Date;

public class BruteForceServlet extends QueryResponseServlet {

    @Override
    public ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
        return FileCumulator.queryForRunningEvent(userDate);
    }

    @Override
    public int queryHeartRatesForDay(Date date) {
        return FileCumulator.queryHeartRatesForDay(date);
    }

    @Override
    public int queryForTotalStepsInDay(Date userDate) {
        return 0;
    }
}