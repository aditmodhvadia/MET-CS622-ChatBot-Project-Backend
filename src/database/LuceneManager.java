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
import sensormodels.*;
import utils.WebAppConstants;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.objects.Global.Infinity;

/**
 * @author Adit Modhvadia
 */
public class LuceneManager implements DatabaseQueryRunner, DbManager {
    public static String indexDirRelativePath = "luceneIndex";

    private static LuceneManager instance;

    private final String indexDir;
    private IndexWriter indexWriter;

    private LuceneManager(String indexDirRealPath) {
        this.indexDir = indexDirRealPath;
    }

    /**
     * Singleton method to get the instance of the class
     *
     * @param servletContext context of the running servlet
     * @return singleton instance of the class
     */
    public static LuceneManager getInstance(ServletContext servletContext) {
        if (instance == null) {
            instance = new LuceneManager(servletContext.getRealPath(indexDirRelativePath));
        }
        return instance;
    }

    @Override
    public ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
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

    @Override
    public int queryHeartRatesForDay(Date date) {
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

    @Override
    public int queryForTotalStepsInDay(Date userDate) {
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

    public <T extends LuceneStoreModel> void storeSensorData(List<T> sensorData) {
        try {
            IndexWriter indexWriter = getIndexWriter();
            if (indexWriter != null) {
                indexWriter.addDocuments(sensorData.stream().map(LuceneStoreModel::getDocument).collect(Collectors.toList()));
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
    private IndexWriter getIndexWriter() {
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

    private void closeWriter() {
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
    private List<Document> getLuceneQueryTime(String queryStr, String indexValue) {
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

    @Override
    public void init() {
        System.out.println("<----Lucene initialized.---->");
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

        public static final String TIMESTAMP = "timestamp";
        public static final String SENSOR_NAME = "sensorName";
    }
}
