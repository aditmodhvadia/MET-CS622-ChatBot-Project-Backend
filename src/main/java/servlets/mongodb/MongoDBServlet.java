package servlets.mongodb;

import database.LuceneManager;
import database.MongoDBManager;
import sensormodels.ActivFitSensorData;
import servlets.queryresponseservlet.QueryResponseServlet;

import java.util.ArrayList;
import java.util.Date;

public class MongoDBServlet extends QueryResponseServlet {
    private final MongoDBManager mongoManager;

    public MongoDBServlet() {
        this.mongoManager = MongoDBManager.getInstance();
    }

    @Override
    public ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
        return mongoManager.queryForRunningEvent(userDate);
    }

    @Override
    public int queryHeartRatesForDay(Date date) {
        return mongoManager.queryHeartRatesForDay(date);
    }

    @Override
    public int queryForTotalStepsInDay(Date userDate) {
        return mongoManager.queryForTotalStepsInDay(userDate);
    }
}
