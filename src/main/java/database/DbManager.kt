package database

import javax.annotation.Nonnull
import javax.servlet.ServletContext

interface DbManager<T> {
    /**
     * Initialize the database.
     *
     * @param servletContext servlet context
     */
    fun init(servletContext: ServletContext?)

    /**
     * Use to insert given documents of the given type into the given target collection.
     *
     * @param sensorDataList given list of sensor data to be inserted
     */
    fun <V : T?> insertSensorDataList(@Nonnull sensorDataList: List<V>)

    /**
     * Use to insert given document of the given type into the given target collection.
     *
     * @param sensorData given sensor data to be inserted
     */
    fun <V : T?> insertSensorData(sensorData: V)
}