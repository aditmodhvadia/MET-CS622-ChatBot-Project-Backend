package servlets.mongodb;

import database.MongoDBManager;
import sensormodels.ActivFitSensorData;
import servlets.queryresponseservlet.QueryResponseServlet;

import java.util.ArrayList;
import java.util.Date;

public class MongoDBServlet extends QueryResponseServlet {
    @Override
    public ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
        return MongoDBManager.queryForRunningEvent(userDate);
    }

    @Override
    public int queryHeartRatesForDay(Date date) {
        return MongoDBManager.queryHeartRatesForDay(date);
    }

    @Override
    public int queryForTotalStepsInDay(Date userDate) {
        return MongoDBManager.queryForTotalStepsInDay(userDate);
    }
}
