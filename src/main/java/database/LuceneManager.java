package database;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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
import sensormodels.activfit.ActivFitSensorData;
import sensormodels.activfit.ActivFitSensorDataBuilder;
import sensormodels.store.models.LuceneStoreModel;
import utils.WebAppConstants;

public class LuceneManager implements DatabaseQueryRunner, DbManager<LuceneStoreModel> {
  public static String indexDirRelativePath = "luceneIndex";
  private static LuceneManager instance;

  private String indexDir;
  private IndexWriter indexWriter;

  private LuceneManager(ServletContext servletContext) {
    init(servletContext);
  }

  /**
   * Singleton method to get the instance of the class.
   *
   * @return singleton instance of the class
   */
  public static LuceneManager getInstance(@Nonnull ServletContext servletContext) {
    if (instance == null) {
      instance = new LuceneManager(servletContext);
    }
    return instance;
  }

  public void updateServletContext(@Nonnull ServletContext servletContext) {
    this.indexDir = servletContext.getRealPath(indexDirRelativePath);
    System.out.println(indexDir);
  }

  @Override
  public ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
    String formattedDate = WebAppConstants.inputDateFormat.format(userDate);

    return getLuceneQueryTime("running", LuceneConstants.ACTIVITY).stream()
        .filter(document -> document.get(LuceneConstants.FORMATTED_DATE).equals(formattedDate))
        .map(
            doc ->
                new ActivFitSensorDataBuilder()
                    .setStartTime(doc.get(LuceneConstants.START_TIME))
                    .setEndTime(doc.get(LuceneConstants.END_TIME))
                    .setActivity(doc.get(LuceneConstants.ACTIVITY))
                    .build())
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Override
  public int queryHeartRatesForDay(Date date) {
    List<Document> results = getLuceneQueryTime("HeartRate", LuceneConstants.SENSOR_NAME);
    String formattedDate = WebAppConstants.inputDateFormat.format(date);

    return (int)
        results.stream()
            .filter(
                document ->
                    Objects.equals(document.get(LuceneConstants.FORMATTED_DATE), formattedDate))
            .count();
  }

  @Override
  public int queryForTotalStepsInDay(Date userDate) {
    List<Document> results = getLuceneQueryTime("Activity", LuceneConstants.SENSOR_NAME);
    String formattedDate = WebAppConstants.inputDateFormat.format(userDate);

    return Integer.parseInt(
        results.stream()
            .filter(doc -> Objects.equals(doc.get(LuceneConstants.FORMATTED_DATE), formattedDate))
            .max(
                Comparator.comparingInt(
                    doc -> Integer.parseInt(doc.get(LuceneConstants.STEP_COUNT))))
            .get()
            .get(LuceneConstants.STEP_COUNT));
  }

  /**
   * Call to get instance of IndexWriter.
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

  /** Close the writer to release write lock. */
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
   * Called to perform search in Lucene in the given indexDirectory for the given query string and
   * the given Index Value.
   *
   * @param queryStr given query string
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
      for (ScoreDoc hit : hits) {
        int docId = hit.doc;
        Document d = searcher.doc(docId);
        results.add(d);
      }
      // reader can only be closed when there
      // is no need to access the documents any more.
      reader.close();
    } catch (ParseException | IOException e) {
      e.printStackTrace();
    }
    //        System.out.println("Search time for " + queryStr + " for index " + indexValue + " is "
    // + searchTime + "ms");
    return results;
  }

  @Override
  public void init(@Nullable ServletContext servletContext) {
    if (servletContext != null) {
      this.updateServletContext(servletContext);
    }
    System.out.println("<----Lucene initialized.---->");
  }

  @Override
  public <V extends LuceneStoreModel> void insertSensorDataList(@Nonnull List<V> sensorDataList) {
    try {
      IndexWriter indexWriter = getIndexWriter();
      if (indexWriter != null) {
        indexWriter.addDocuments(
            sensorDataList.stream()
                .map(LuceneStoreModel::getDocument)
                .collect(Collectors.toList()));
        System.out.println(
            "Lucene Log: Data Inserted for " + sensorDataList.get(0).getClass().getSimpleName());
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeWriter();
    }
  }

  @Override
  public <V extends LuceneStoreModel> void insertSensorData(V sensorData) {
    try {
      IndexWriter indexWriter = getIndexWriter();
      if (indexWriter != null) {
        indexWriter.addDocument(sensorData.getDocument());
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeWriter();
    }
  }

  /** Constants class. */
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
