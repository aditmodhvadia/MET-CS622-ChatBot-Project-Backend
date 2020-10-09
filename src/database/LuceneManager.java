package database;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import sensormodels.ActivFitSensorData;
import sensormodels.ActivitySensorData;
import sensormodels.HeartRateSensorData;
import sensormodels.LightSensorData;
import utils.WebAppConstants;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jdk.nashorn.internal.objects.Global.Infinity;

/**
 * @author Adit Modhvadia
 */
public class LuceneManager {

    private static String indexDir;
    private static IndexWriter indexWriter;

    /**
     * Get the result from ActivFit Sensor Data for the given Date, if there is a running event for it
     *
     * @param userDate given Date
     * @return List of ActivFitSensorData having running activity for the given Date
     */
    public static ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
        ArrayList<ActivFitSensorData> queryResult = new ArrayList<>();  // holds the result from the query
        String formattedDate = WebAppConstants.inputDateFormat.format(userDate);
        for (Document doc :
                getLuceneQueryTime("running", LuceneConstants.ACTIVITY)) {
            if (doc.get(LuceneConstants.FORMATTED_DATE).equals(formattedDate)) {
                ActivFitSensorData data = new ActivFitSensorData();
                ActivFitSensorData.Timestamp timeStamp = new ActivFitSensorData.Timestamp();
                timeStamp.setStartTime(doc.get(LuceneConstants.START_TIME));
                timeStamp.setEndTime(doc.get(LuceneConstants.END_TIME));
                data.setTimestamp(timeStamp);
                ActivFitSensorData.SensorData sensorData = new ActivFitSensorData.SensorData();
                sensorData.setActivity(doc.get(LuceneConstants.ACTIVITY));
                data.setSensorData(sensorData);
                data.setFormattedDate();
                queryResult.add(data);
            }
        }
        return queryResult;
    }

    /**
     * Called to query and fetch all days heart rate data to count the number of notifications user receives for the day
     *
     * @param date
     * @return int value of the number of notifications received by the user for heart rates
     */
    public static int queryHeartRatesForDay(Date date) {
        List<Document> results = getLuceneQueryTime("HeartRate", LuceneConstants.SENSOR_NAME);
        System.out.println(results.size());
        int heartRateCounter = 0;
        for (Document doc :
                results) {
            System.out.println("Document found");
            if (doc.get(LuceneConstants.FORMATTED_DATE) != null && doc.get(LuceneConstants.FORMATTED_DATE).equals(WebAppConstants.inputDateFormat.format(date))) {
                heartRateCounter++;
            }
        }
        return heartRateCounter;
    }

    /**
     * Called to query the number of steps user takes for the given day
     *
     * @param userDate given day
     * @return step count for the given day
     */
    public static int queryForTotalStepsInDay(Date userDate) {
        List<Document> results = getLuceneQueryTime("Activity", LuceneConstants.SENSOR_NAME);
        String formattedDate = WebAppConstants.inputDateFormat.format(userDate);
        int maxStepCount = (int) -Infinity;    // Max value of step count for the day
        for (Document doc :
                results) {
            System.out.println("Document found");
            if (doc.get(LuceneConstants.FORMATTED_DATE) != null && doc.get(LuceneConstants.FORMATTED_DATE).equals(formattedDate)
                    && doc.get(LuceneConstants.STEP_COUNT) != null && Integer.parseInt(doc.get(LuceneConstants.STEP_COUNT)) > maxStepCount) {
                maxStepCount = Integer.parseInt(doc.get(LuceneConstants.STEP_COUNT));
            }
        }
        return maxStepCount;
    }

    /**
     * Call to store given activfit sensor data
     *
     * @param activFitSensorDataList
     */
    public static void storeActivFitSensorData(List<ActivFitSensorData> activFitSensorDataList) {
        try {
            IndexWriter indexWriter = getIndexWriter();
//            index ActivFitSensorData
            if (indexWriter != null) {
                for (ActivFitSensorData sensorData :
                        activFitSensorDataList) {
                    addActivFitData(indexWriter, sensorData);
                }
            }
            closeWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Call to store given heart rate sensor data
     *
     * @param heartRateSensorDataList given list of heart rate sensor data
     */
    public static void storeHeartRateSensorData(List<HeartRateSensorData> heartRateSensorDataList) {
        try {
            IndexWriter indexWriter = getIndexWriter();
//            index HeartRateSensorData
            if (indexWriter != null) {
                for (HeartRateSensorData sensorData :
                        heartRateSensorDataList) {
                    addHeartRateDoc(indexWriter, sensorData);
                }
            }
            closeWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Call to store given list of activity sensor data
     *
     * @param activitySensorDataList given list of sensor data
     */
    public static void storeActivitySensorData(List<ActivitySensorData> activitySensorDataList) {
        try {
            IndexWriter indexWriter = getIndexWriter();
//            index HeartRateSensorData
            if (indexWriter != null) {
                for (ActivitySensorData sensorData :
                        activitySensorDataList) {
                    addActivityDoc(indexWriter, sensorData);
                }
            }
            closeWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Call to get instance of IndexWriter
     *
     * @return index writer
     */
    private static IndexWriter getIndexWriter() {
        try {
            // 0. Specify the analyzer for tokenizing text.
            // 1. create the index
            Directory dir = FSDirectory.open(Paths.get(indexDir));

            // The same analyzer should be used for indexing and searching
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(dir, config);
            return indexWriter;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void closeWriter() {
        if (indexWriter != null) {
            try {
                indexWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called to perform search in Lucene in the given indexDirectory for the given query string and the given Index Value
     *
     * @param queryStr   given query string
     * @param indexValue given index value
     * @return time in milliseconds to perform the query
     */
    private static List<Document> getLuceneQueryTime(String queryStr, String indexValue) {
        List<Document> results = new ArrayList<>();
        try {
            // 0. Specify the analyzer for tokenizing text.
            // The same analyzer should be used for indexing and searching
            StandardAnalyzer analyzer = new StandardAnalyzer();
            // 1. create the index
            Directory index = FSDirectory.open(Paths.get(indexDir));

            Query q = new QueryParser(indexValue, analyzer).parse(queryStr);
            // 3. search
            int hitsPerPage = 100;
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);

            ScoreDoc[] hits = docs.scoreDocs;

//            System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                results.add(d);
//                System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
//                System.out.println((i + 1) + ". " + d.get(LuceneConstants.BPM) + "\t" + d.get(LuceneConstants.SENSOR_NAME));
                /*System.out.println((i + 1) + ". " + d.get(LuceneConstants.SENSOR_NAME) + "\t" + d.get(indexValue)
                        + "\t" + d.get(LuceneConstants.TIMESTAMP));*/
            }
            // reader can only be closed when there
            // is no need to access the documents any more.
            reader.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
//        System.out.println("Search time for " + queryStr + " for index " + indexValue + " is " + searchTime + "ms");
        return results;
    }

    /**
     * Call to insert given HeartRateSensorData using the given IndexWriter in a document
     *
     * @param w          given IndexWriter
     * @param sensorData given heart rate sensor data
     * @throws IOException
     */
    private static void addHeartRateDoc(IndexWriter w, HeartRateSensorData sensorData) throws IOException {
        Document doc = new Document();
//        doc.add(new TextField(LuceneConstants.BPM, String.valueOf(sensorData.getSensorData().getBpm()), Field.Store.YES));
        doc.add(new IntPoint(LuceneConstants.BPM, sensorData.getSensorData().getBpm()));
        doc.add(new StringField(LuceneConstants.SENSOR_NAME, sensorData.getSensorName(), Field.Store.YES));
        doc.add(new StringField(LuceneConstants.FORMATTED_DATE, sensorData.getFormatted_date(), Field.Store.YES));
//         use a string field for timestamp because we don't want it tokenized
        doc.add(new StringField(LuceneConstants.TIMESTAMP, sensorData.getTimestamp(), Field.Store.YES));
        w.addDocument(doc);
    }

    /**
     * Call to insert given ActivitySensorData using the given IndexWriter in a document
     *
     * @param w          given IndexWriter
     * @param sensorData given activity sensor data
     * @throws IOException
     */
    private static void addActivityDoc(IndexWriter w, ActivitySensorData sensorData) throws IOException {
        Document doc = new Document();
        doc.add(new IntPoint(LuceneConstants.STEP_COUNT, sensorData.getSensorData().getStepCounts()));
        doc.add(new IntPoint(LuceneConstants.STEP_DELTA, sensorData.getSensorData().getStepDelta()));
        doc.add(new StringField(LuceneConstants.SENSOR_NAME, sensorData.getSensorName(), Field.Store.YES));
        doc.add(new StringField(LuceneConstants.FORMATTED_DATE, sensorData.getSensorName(), Field.Store.YES));
//         use a string field for timestamp because we don't want it tokenized
        doc.add(new StringField(LuceneConstants.TIMESTAMP, sensorData.getTimestamp(), Field.Store.YES));
        w.addDocument(doc);
    }

    /**
     * Call to insert given LightSensorData using the given IndexWriter in a document
     *
     * @param w          given IndexWriter
     * @param sensorData given heart rate sensor data
     * @throws IOException
     */
    private static void addLightSensorData(IndexWriter w, LightSensorData sensorData) throws IOException {
        Document doc = new Document();
        doc.add(new TextField(LuceneConstants.LUX, sensorData.getLuxValue(), Field.Store.YES));
        doc.add(new StringField(LuceneConstants.SENSOR_NAME, sensorData.getSensorName(), Field.Store.YES));
        doc.add(new StringField(LuceneConstants.FORMATTED_DATE, sensorData.getFormatted_date(), Field.Store.YES));
        //         use a string field for timestamp because we don't want it tokenized
        doc.add(new StringField(LuceneConstants.TIMESTAMP, sensorData.getTimestamp(), Field.Store.YES));
        w.addDocument(doc);
    }

    /**
     * Call to insert given ActivFitSensorData using the given IndexWriter in a document
     *
     * @param w          given IndexWriter
     * @param sensorData given heart rate sensor data
     * @throws IOException
     */
    private static void addActivFitData(IndexWriter w, ActivFitSensorData sensorData) throws IOException {
        Document doc = new Document();
        doc.add(new TextField(LuceneConstants.ACTIVITY, sensorData.getSensorData().getActivity(), Field.Store.YES));
        doc.add(new StringField(LuceneConstants.SENSOR_NAME, sensorData.getSensorName(), Field.Store.YES));
        doc.add(new StringField(LuceneConstants.FORMATTED_DATE, sensorData.getFormatted_date(), Field.Store.YES));
        doc.add(new StringField(LuceneConstants.START_TIME, sensorData.getTimestamp().getStartTime(), Field.Store.YES));
        doc.add(new StringField(LuceneConstants.END_TIME, sensorData.getTimestamp().getEndTime(), Field.Store.YES));
        //         use a string field for timestamp because we don't want it tokenized
        doc.add(new StringField(LuceneConstants.TIMESTAMP, sensorData.getTimestamp().getStartTime(), Field.Store.YES));
        w.addDocument(doc);
    }

    public static void init(String indexDirFromContext) {
        indexDir = indexDirFromContext;
    }

    /**
     * Constants class
     */
    public static class LuceneConstants {

        public static final String LUX = "lux";
        public static final String BPM = "bpm";
        public static final String ACTIVITY = "activity";
        public static final String STEP_COUNT = "step_counts";
        public static final String STEP_DELTA = "step_delta";
        public static final String FORMATTED_DATE = "formattedDate";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";

        static final String TIMESTAMP = "timestamp";
        static final String SENSOR_NAME = "sensorName";
    }
}