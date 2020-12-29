package database

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.ParseException
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import sensormodels.activfit.ActivFitSensorData
import sensormodels.activfit.ActivFitSensorDataBuilder
import sensormodels.store.models.LuceneStoreModel
import utils.WebAppConstants
import java.io.IOException
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import javax.annotation.Nonnull
import javax.servlet.ServletContext
import kotlin.collections.ArrayList

class LuceneManager private constructor(servletContext: ServletContext?) : DatabaseQueryRunner,
    DbManager<LuceneStoreModel?> {
    private var indexDir: String? = null
    private var indexWriter: IndexWriter? = null
    private fun updateServletContext(@Nonnull servletContext: ServletContext) {
        indexDir = servletContext.getRealPath(indexDirRelativePath)
        println(indexDir)
    }

    override fun queryForRunningEvent(userDate: Date?): java.util.ArrayList<ActivFitSensorData> {
        val formattedDate = WebAppConstants.inputDateFormat.format(userDate)
        return getLuceneQueryTime("running", LuceneConstants.ACTIVITY).stream()
            .filter { document: Document -> document[LuceneConstants.FORMATTED_DATE] == formattedDate }
            .map { doc: Document ->
                ActivFitSensorDataBuilder()
                    .setStartTime(doc[LuceneConstants.START_TIME])
                    .setEndTime(doc[LuceneConstants.END_TIME])
                    .setActivity(doc[LuceneConstants.ACTIVITY])
                    .build()
            }
            .collect(Collectors.toCollection { ArrayList() })
    }

    override fun queryHeartRatesForDay(date: Date?): Int {
        val results = getLuceneQueryTime("HeartRate", LuceneConstants.SENSOR_NAME)
        val formattedDate = WebAppConstants.inputDateFormat.format(date)
        return results.stream()
            .filter { document: Document -> document[LuceneConstants.FORMATTED_DATE] == formattedDate }
            .count().toInt()
    }

    override fun queryForTotalStepsInDay(userDate: Date?): Int {
        val results = getLuceneQueryTime("Activity", LuceneConstants.SENSOR_NAME)
        val formattedDate = WebAppConstants.inputDateFormat.format(userDate)
        return results.stream()
            .filter { doc: Document -> doc[LuceneConstants.FORMATTED_DATE] == formattedDate }
            .max(
                Comparator.comparingInt { doc: Document -> doc[LuceneConstants.STEP_COUNT].toInt() })
            .get()[LuceneConstants.STEP_COUNT].toInt()
    }

    /**
     * Call to get instance of IndexWriter.
     *
     * @return index writer
     */
    private fun getIndexWriter(): IndexWriter? {
        try {
            // 0. Specify the analyzer for tokenizing text.
            // 1. create the index
            val dir: Directory = FSDirectory.open(Paths.get(indexDir))
            // The same analyzer should be used for indexing and searching
            val analyzer = StandardAnalyzer()
            val config = IndexWriterConfig(analyzer)
            indexWriter = IndexWriter(dir, config)
            return indexWriter
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /** Close the writer to release write lock.  */
    private fun closeWriter() {
        if (indexWriter != null) {
            try {
                indexWriter!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
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
    private fun getLuceneQueryTime(queryStr: String, indexValue: String): List<Document> {
        val results: MutableList<Document> = java.util.ArrayList()
        try {
            // 0. Specify the analyzer for tokenizing text.
            // The same analyzer should be used for indexing and searching
            val analyzer = StandardAnalyzer()
            // 1. create the index
            val index: Directory = FSDirectory.open(Paths.get(indexDir))
            val q = QueryParser(indexValue, analyzer).parse(queryStr)
            // 3. search
            val hitsPerPage = 100
            val reader: IndexReader = DirectoryReader.open(index)
            val searcher = IndexSearcher(reader)
            val docs = searcher.search(q, hitsPerPage)
            val hits = docs.scoreDocs

            //            System.out.println("Found " + hits.length + " hits.");
            for (hit in hits) {
                val docId = hit.doc
                val d = searcher.doc(docId)
                results.add(d)
            }
            // reader can only be closed when there
            // is no need to access the documents any more.
            reader.close()
        } catch (e: ParseException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //        System.out.println("Search time for " + queryStr + " for index " + indexValue + " is "
        // + searchTime + "ms");
        return results
    }

    override fun init(servletContext: ServletContext?) {
        if (servletContext != null) {
            updateServletContext(servletContext)
        }
        println("<----Lucene initialized.---->")
    }

    override fun insertSensorDataList(@Nonnull sensorDataList: List<LuceneStoreModel?>) {
        try {
            val indexWriter = getIndexWriter()
            if (indexWriter != null) {
                indexWriter.addDocuments(
                    sensorDataList.stream()
                        .map { obj: LuceneStoreModel? -> obj!!.document }
                        .collect(Collectors.toList()))
                println(
                    "Lucene Log: Data Inserted for " + sensorDataList[0]!!::class.java.simpleName
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            closeWriter()
        }
    }

    override fun insertSensorData(sensorData: LuceneStoreModel?) {
        try {
            val indexWriter = getIndexWriter()
            indexWriter?.addDocument(sensorData!!.document)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            closeWriter()
        }
    }

    /** Constants class.  */
    object LuceneConstants {
        const val LUX = "lux"
        const val BPM = "bpm"
        const val ACTIVITY = "activity"
        const val STEP_COUNT = "step_counts"
        const val STEP_DELTA = "step_delta"
        const val FORMATTED_DATE = "formattedDate"
        const val START_TIME = "start_time"
        const val END_TIME = "end_time"
        const val TIMESTAMP = "timestamp"
        const val SENSOR_NAME = "sensorName"
    }

    companion object {
        var indexDirRelativePath = "luceneIndex"
        private var instance: LuceneManager? = null

        /**
         * Singleton method to get the instance of the class.
         *
         * @return singleton instance of the class
         */
        @JvmStatic
        fun getInstance(@Nonnull servletContext: ServletContext?): LuceneManager? {
            if (instance == null) {
                instance = LuceneManager(servletContext)
            }
            return instance
        }
    }

    init {
        init(servletContext)
    }
}