package servlets.mysql;

import database.MySqlManager;
import sensormodels.ActivFitSensorData;
import servlets.queryresponseservlet.QueryResponseServlet;

import java.util.ArrayList;
import java.util.Date;

public class MySQLServlet extends QueryResponseServlet {

    @Override
    public ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
        return MySqlManager.queryForRunningEvent(userDate);
    }

    @Override
    public int queryHeartRatesForDay(Date date) {
        return MySqlManager.queryHeartRatesForDay(date);
    }

    @Override
    public int queryForTotalStepsInDay(Date userDate) {
        return MySqlManager.queryForTotalStepsInDay(userDate);
    }
}
