package lucene;

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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Adit Modhvadia
 */
public class LuceneManager {
    public static String indexDir = "luceneIndex";

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
            return new IndexWriter(dir, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Called to perform search in Lucene in the given indexDirectory for the given query string and the given Index Value
     *
     * @param queryStr   given query string
     * @param indexValue given index value
     * @return time in milliseconds to perform the query
     */
    public static long getLuceneQueryTime(String queryStr, String indexValue) {
        long searchTime = System.currentTimeMillis();
        try {
            // 0. Specify the analyzer for tokenizing text.
            // The same analyzer should be used for indexing and searching
            StandardAnalyzer analyzer = new StandardAnalyzer();
            // 1. create the index
            Directory index = FSDirectory.open(Paths.get(indexDir));

            Query q = new QueryParser(indexValue, analyzer).parse(queryStr);
            // 3. search
            int hitsPerPage = 10;
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);

//            search complete, record end time to calculate total time
            searchTime = System.currentTimeMillis() - searchTime;
            ScoreDoc[] hits = docs.scoreDocs;

//            System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
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
        return searchTime;
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
        //         use a string field for timestamp because we don't want it tokenized
        doc.add(new StringField(LuceneConstants.TIMESTAMP, sensorData.getTimestamp().getStartTime(), Field.Store.YES));
        w.addDocument(doc);
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

        static final String TIMESTAMP = "timestamp";
        static final String SENSOR_NAME = "sensorName";
    }
}
