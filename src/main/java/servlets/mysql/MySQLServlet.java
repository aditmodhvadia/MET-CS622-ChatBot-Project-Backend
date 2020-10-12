package servlets.mysql;

import database.MySqlManager;
import sensormodels.ActivFitSensorData;
import servlets.queryresponseservlet.QueryResponseServlet;

import java.util.ArrayList;
import java.util.Date;

public class MySQLServlet extends QueryResponseServlet {
    private final MySqlManager mySqlManager;

    public MySQLServlet() {
        this.mySqlManager = MySqlManager.getInstance();
    }

    @Override
    public ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
        return this.mySqlManager.queryForRunningEvent(userDate);
    }

    @Override
    public int queryHeartRatesForDay(Date date) {
        return this.mySqlManager.queryHeartRatesForDay(date);
    }

    @Override
    public int queryForTotalStepsInDay(Date userDate) {
        return this.mySqlManager.queryForTotalStepsInDay(userDate);
    }
}
