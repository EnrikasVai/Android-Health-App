@file:Suppress("DEPRECATION")

package com.example.bakis

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.Field
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION", "NAME_SHADOWING")
class GoogleFitDataHandler(private val context: Context) {

    fun readFitnessData(listener: TodayDataListener) {
        val startCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        startCalendar.add(Calendar.DAY_OF_YEAR, 1)
        val endTime = startCalendar.timeInMillis - 1

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
            .aggregate(DataType.TYPE_MOVE_MINUTES, DataType.AGGREGATE_MOVE_MINUTES)
            .aggregate(DataType.TYPE_SPEED, DataType.AGGREGATE_SPEED_SUMMARY)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val buckets = response.buckets
                var totalDistance = 0.0
                var moveMinutes = 0.0
                val averageSpeed = 0.0
                buckets.forEach { bucket ->
                    bucket.dataSets.forEach { dataSet ->
                        when (dataSet.dataType) {
                            DataType.TYPE_DISTANCE_DELTA -> totalDistance += dataSet.dataPoints.sumOf { it.getValue(Field.FIELD_DISTANCE).asFloat().toDouble() }
                            DataType.TYPE_MOVE_MINUTES -> moveMinutes += dataSet.dataPoints.sumOf { it.getValue(Field.FIELD_DURATION).asInt().toLong() }
                        }
                    }
                }
                listener.onStepDataReceived(totalDistance, moveMinutes, averageSpeed)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFit", "There was a problem reading the data.", e)
                listener.onError(e)
            }
    }
    interface TodayDataListener {
        fun onStepDataReceived(distance: Double, moveMinutes: Double, averageSpeed: Double)
        fun onError(e: Exception)
    }
    //TODAYS CALORIES EATEN
    interface TodayCaloriesListener {
        fun onCaloriesDataReceived(calories: Double)
        fun onError(e: Exception)
    }

    fun readTodayCaloriesData(listener: TodayCaloriesListener) {
        val startCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        startCalendar.add(Calendar.DAY_OF_YEAR, 1)
        val endTime = startCalendar.timeInMillis - 1

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_NUTRITION)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                var totalCalories = 0.0
                response.dataSets.forEach { dataSet ->
                    dataSet.dataPoints.forEach { dataPoint ->
                        val calories = dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue(Field.NUTRIENT_CALORIES)
                        if (calories != null) {
                            totalCalories += calories
                        }
                    }
                }
                listener.onCaloriesDataReceived(totalCalories)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitCalories", "There was a problem reading the nutrition data.", e)
                listener.onError(e)
            }
    }
    fun readPastWeekCaloriesData(listener: CalorieDataListener) {
        val endCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1) // Move to the end of today
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endTime = endCalendar.timeInMillis - 1

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6) // Go back 6 days from today
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val dailyCalories = MutableList(7) { 0.0 }

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_NUTRITION, DataType.AGGREGATE_NUTRITION_SUMMARY)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                response.buckets.forEachIndexed { index, bucket ->
                    var totalCaloriesForDay = 0.0
                    bucket.dataSets.forEach { dataSet ->
                        dataSet.dataPoints.forEach { dataPoint ->
                            val calories = dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue(Field.NUTRIENT_CALORIES)
                            calories?.let {
                                totalCaloriesForDay += it
                            }
                        }
                    }
                    dailyCalories[index] = totalCaloriesForDay
                }
                listener.onCaloriesDataReceived(dailyCalories)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitCaloriesWeek", "There was a problem reading the nutrition data for the past week.", e)
                listener.onError(e)
            }
    }
    interface CalorieDataListener {
        fun onCaloriesDataReceived(dailyCalories: List<Double>)
        fun onError(e: Exception)
    }
    //Nutrition by month
    fun readLastTwelveMonthsCaloriesData(listener: CalorieDataListener) {
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.MONTH, -11)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        // Pre-populate the map for the last 12 months with zeros
        val monthlyCaloriesData = mutableMapOf<Pair<Int, Int>, Pair<Double, Int>>()
        for (i in 0 until 12) {
            val cal = Calendar.getInstance().apply {
                add(Calendar.MONTH, -i)
                set(Calendar.DAY_OF_MONTH, 1) // Ensure consistency
            }
            val yearMonth = Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
            monthlyCaloriesData[yearMonth] = Pair(0.0, 0) // Initialize to zero calories and zero days
        }

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_NUTRITION, DataType.AGGREGATE_NUTRITION_SUMMARY)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val dailyCaloriesData = response.buckets.flatMap { bucket ->
                    bucket.dataSets.flatMap { dataSet ->
                        dataSet.dataPoints.mapNotNull { dataPoint ->
                            val calories = dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue(Field.NUTRIENT_CALORIES)
                            val date = dataPoint.getStartTime(TimeUnit.MILLISECONDS)
                            calories?.let { Pair(date, it) }
                        }
                    }
                }

                // Update the map with actual data
                dailyCaloriesData.forEach { (date, calories) ->
                    val cal = Calendar.getInstance().apply { timeInMillis = date }
                    val yearMonth = Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
                    val existing = monthlyCaloriesData[yearMonth]!!
                    monthlyCaloriesData[yearMonth] = Pair(existing.first + calories.toDouble(), existing.second + 1)
                }

                val completeMonthlyAverageData = monthlyCaloriesData.map { (yearMonth, caloriesAndCount) ->
                    Pair(yearMonth, if (caloriesAndCount.second > 0) caloriesAndCount.first / caloriesAndCount.second else 0.0)
                }.sortedWith(compareBy({ it.first.first }, { it.first.second }))

                listener.onCaloriesDataReceived(completeMonthlyAverageData.map { it.second }) // Map to just the averages
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitMonthsCalories", "There was a problem reading the nutrition data.", e)
                listener.onError(e)
            }
    }





    //MOVE MINUTES DATA WEEKLY
    interface MoveMinutesListener{
        fun onMoveMinutesDataReceived(moveMinutes: List<Int>)
        fun onError(e: Exception)
    }

    fun readWeekMoveMinutesData(listener: MoveMinutesListener) {
        // Calculate the start of the week (7 days ago at 00:00:00.000)
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_MOVE_MINUTES, DataType.AGGREGATE_MOVE_MINUTES)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val moveMinutes = response.buckets.map { bucket ->
                    if (bucket.dataSets.isNotEmpty()) {
                        bucket.dataSets.flatMap { it.dataPoints }.sumOf { it.getValue(Field.FIELD_DURATION).asInt() }
                    } else {
                        0
                    }
                }

                listener.onMoveMinutesDataReceived(moveMinutes)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitWeek", "There was a problem reading the data.", e)
                listener.onError(e)
            }
    }
    //MOVE MINUTES MONTHS
    fun readLastTwelveMonthsMoveMinutesData(listener: MoveMinutesListener) {
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.MONTH, -11)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val initialMonthlyData = (0..11).associate {
            val cal = Calendar.getInstance().apply {
                add(Calendar.MONTH, -it)
            }
            Pair(Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)), 0)
        }.toMutableMap()

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_MOVE_MINUTES, DataType.AGGREGATE_MOVE_MINUTES)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val dailyMoveMinutes = response.buckets.mapNotNull { bucket ->
                    val date = bucket.getStartTime(TimeUnit.MILLISECONDS)
                    bucket.dataSets.flatMap { it.dataPoints }
                        .sumOf { it.getValue(Field.FIELD_DURATION).asInt() }
                        .takeIf { it > 0 }?.let { Pair(date, it) }
                }

                val monthlyData = dailyMoveMinutes.groupBy {
                    val cal = Calendar.getInstance().apply { timeInMillis = it.first }
                    Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
                }.mapValues { (_, value) ->
                    value.sumOf { it.second } / value.size
                }

                // Update initialMonthlyData with actual data
                monthlyData.forEach { (key, value) ->
                    initialMonthlyData[key] = value
                }

                val sortedMonthlyData = initialMonthlyData.toSortedMap(compareBy({ it.first }, { it.second }))
                val finalMonthlyAverages = sortedMonthlyData.values.toList()

                listener.onMoveMinutesDataReceived(finalMonthlyAverages)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitMonths", "There was a problem reading the data.", e)
                listener.onError(e)
            }
    }

    //DISTANCE DATA WEEK
    interface DistanceDataListener {
        fun onDistanceDataReceived(distanceData: List<Float>)
        fun onError(e: Exception)
    }
    fun readWeekDistanceData(listener: DistanceDataListener) {
        // Calculate the start of the week (7 days ago at 00:00:00.000)
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6) // Move back 6 days to cover a total of 7 days including today
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val distanceData = response.buckets.mapNotNull { bucket ->
                    bucket.dataSets.flatMap { it.dataPoints }.firstNotNullOfOrNull {
                        it.getValue(Field.FIELD_DISTANCE).asFloat()
                    } // Take the first or return null if empty
                } // Ensure no null values are included

                listener.onDistanceDataReceived(distanceData)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitWeekDistance", "There was a problem reading the distance data.", e)
                listener.onError(e)
            }
    }
    fun readLastTwelveMonthsDistanceData(listener: DistanceDataListener) {
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.MONTH, -11)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val dailyDistanceData = response.buckets.flatMap { bucket ->
                    bucket.dataSets.flatMap { dataSet ->
                        dataSet.dataPoints.mapNotNull { dataPoint ->
                            dataPoint.getValue(Field.FIELD_DISTANCE).asFloat().takeIf { it > 0 }?.let { distance ->
                                val date = dataPoint.getStartTime(TimeUnit.MILLISECONDS)
                                Pair(date, distance)
                            }
                        }
                    }
                }

                // Aggregate daily data into monthly average data
                val monthlyAverageData = mutableMapOf<Pair<Int, Int>, MutableList<Float>>()

                dailyDistanceData.forEach { (date, distance) ->
                    val cal = Calendar.getInstance().apply { timeInMillis = date }
                    val yearMonth = Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
                    monthlyAverageData.getOrPut(yearMonth) { mutableListOf() }.add(distance)
                }

                val completeMonthlyData = (0 until 12).map { offset ->
                    val cal = Calendar.getInstance().apply {
                        add(Calendar.MONTH, -offset)
                    }
                    val yearMonth = Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
                    val distances = monthlyAverageData[yearMonth]
                    if (!distances.isNullOrEmpty()) {
                        distances.average().toFloat()
                    } else {
                        0f
                    }
                }.reversed() // This makes sure the current month's data is last

                listener.onDistanceDataReceived(completeMonthlyData)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitMonthsDistance", "There was a problem reading the distance data.", e)
                listener.onError(e)
            }
    }




    //Functions for retrieving Today's data steps, bpm, sleep, calories. Currently only used in home screen to show today's health data.
    interface StepDataListener {
        fun onStepDataReceived(stepCount: Int)
        fun onError(e: Exception)
    }

    fun readStepData(listener: StepDataListener) {
        val startCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        startCalendar.add(Calendar.DAY_OF_YEAR, 1)
        val endTime = startCalendar.timeInMillis - 1

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val dataSet = response.buckets.flatMap { it.dataSets }.flatMap { it.dataPoints }
                val totalSteps = dataSet.sumOf { it.getValue(Field.FIELD_STEPS).asInt() }
                listener.onStepDataReceived(totalSteps)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFit", "There was a problem reading the data.", e)
                listener.onError(e)
            }
    }



    interface SleepDataListener {
        fun onSleepDataReceived(totalSleepMinutes: Int)
        fun onError(e: Exception)
    }

    fun readSleepData(listener: SleepDataListener) {
        val startCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        startCalendar.add(Calendar.DAY_OF_YEAR, 1)
        val endTime = startCalendar.timeInMillis - 1

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_SLEEP_SEGMENT)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                // The sleep data response is a bit different; we need to interpret the segments
                var totalSleepMinutes = 0
                response.dataSets.flatMap { it.dataPoints }.forEach { dataPoint ->
                    val startTime = dataPoint.getStartTime(TimeUnit.MINUTES)
                    val endTime = dataPoint.getEndTime(TimeUnit.MINUTES)
                    totalSleepMinutes += (endTime - startTime).toInt()
                }
                listener.onSleepDataReceived(totalSleepMinutes)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitSleep", "There was a problem reading the sleep data.", e)
                listener.onError(e)
            }
    }

    interface CaloriesDataListener {
        fun onCalDataReceived(calCount: Int)
        fun onError(e: Exception)
    }
    fun readCaloriesData(listener: CaloriesDataListener) {
        val startCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        startCalendar.add(Calendar.DAY_OF_YEAR, 1)
        val endTime = startCalendar.timeInMillis - 1

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                var totalCalories = 0.0
                response.buckets.flatMap { it.dataSets }.flatMap { it.dataPoints }.forEach { dataPoint ->
                    totalCalories += dataPoint.getValue(Field.FIELD_CALORIES).asFloat()
                }
                listener.onCalDataReceived(totalCalories.toInt())
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitCalories", "There was a problem reading the calories data.", e)
                listener.onError(e)
            }
    }
    interface HeartRateDataListener {
        fun onHeartRateDataReceived(averageHeartRate: Float)
        fun onError(e: Exception)
    }
    fun readHeartRateData(listener: HeartRateDataListener) {
        val startCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        startCalendar.add(Calendar.DAY_OF_YEAR, 1)
        val endTime = startCalendar.timeInMillis - 1

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                // Here, we need to calculate the average heart rate from the aggregated data
                var totalHeartRate = 0.0
                var dataPointsCount = 0
                response.buckets.flatMap { it.dataSets }.flatMap { it.dataPoints }.forEach { dataPoint ->
                    totalHeartRate += dataPoint.getValue(Field.FIELD_AVERAGE).asFloat()
                    dataPointsCount++
                }
                val averageHeartRate = if (dataPointsCount > 0) totalHeartRate / dataPointsCount else 0.0
                listener.onHeartRateDataReceived(averageHeartRate.toFloat())
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitHeartRate", "There was a problem reading the heart rate data.", e)
                listener.onError(e)
            }
    }
    //resting bpm today
    fun readRestingHeartRateData(listener: HeartRateDataListener) {
        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1) // Move to yesterday
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis
        startCalendar.add(Calendar.DAY_OF_YEAR, 1)
        val endTime = startCalendar.timeInMillis - 1

        val dataSource = DataSource.Builder()
            .setType(DataSource.TYPE_DERIVED)
            .setDataType(DataType.TYPE_HEART_RATE_BPM)
            .setAppPackageName("com.google.android.gms")
            .setStreamName("resting_heart_rate<-merge_heart_rate_bpm")
            .build()

        val readRequest = DataReadRequest.Builder()
            .aggregate(dataSource)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                var totalRestingHeartRate = 0.0
                var dataPointsCount = 0

                response.buckets.flatMap { it.dataSets }.flatMap { it.dataPoints }.forEach { dataPoint ->
                    val heartRate = dataPoint.getValue(Field.FIELD_AVERAGE).asFloat()
                    totalRestingHeartRate += heartRate
                    dataPointsCount++
                }

                val restingHeartRateAverage = if (dataPointsCount > 0) totalRestingHeartRate / dataPointsCount else 0.0

                // Callback or handle the single resting BPM value
                listener.onHeartRateDataReceived(restingHeartRateAverage.toFloat())
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitRestingHeartRate", "There was a problem reading the resting heart rate data.", e)
                listener.onError(e)
            }
    }
    //resting bpm WEEK
    fun readWeekRestingHeartRateData(listener: HeartRateDataWeekListener) {
        // Calculate start of the week (7 days ago at 00:00:00.000)
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6) // Move back 6 days to cover a total of 7 days including today
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val dataSource = DataSource.Builder()
            .setType(DataSource.TYPE_DERIVED)
            .setDataType(DataType.TYPE_HEART_RATE_BPM)
            .setAppPackageName("com.google.android.gms")
            .setStreamName("resting_heart_rate<-merge_heart_rate_bpm")
            .build()

        val readRequest = DataReadRequest.Builder()
            .aggregate(dataSource)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val heartRateCounts = mutableListOf<Float>()

                for (i in 1..7) {
                    heartRateCounts.add(0.0f)
                }

                response.buckets.forEachIndexed { index, bucket ->
                    val dataSet = bucket.dataSets.firstOrNull()
                    if (dataSet != null && dataSet.dataPoints.isNotEmpty()) {
                        val averageRestingHeartRate = dataSet.dataPoints.map { it.getValue(Field.FIELD_AVERAGE).asFloat() }.average().toFloat()
                        heartRateCounts[index] = averageRestingHeartRate
                    }
                }

                listener.onHeartRateDataReceived(heartRateCounts)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitRestingHeartRateWeek", "There was a problem reading the data.", e)
                listener.onError(e)
            }
    }
    //resting bpm MONTH
    fun readMonthlyAverageRestingHeartRate(listener: HeartRateDataMonthListener) {
        val endCalendar = Calendar.getInstance()
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.YEAR, -1)
            add(Calendar.MONTH, +1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val dataSource = DataSource.Builder()
            .setType(DataSource.TYPE_DERIVED)
            .setDataType(DataType.TYPE_HEART_RATE_BPM)
            .setAppPackageName("com.google.android.gms")
            .setStreamName("resting_heart_rate<-merge_heart_rate_bpm")
            .build()

        val readRequest = DataReadRequest.Builder()
            .aggregate(dataSource)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val monthlyHeartRates = mutableMapOf<String, Float>()

                // Initialize every month with 0f to ensure all months are represented even if no data exists
                val tempCalendar = Calendar.getInstance().apply { timeInMillis = startTime }
                while (tempCalendar.before(endCalendar) || tempCalendar == endCalendar) {
                    val monthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(tempCalendar.time)
                    monthlyHeartRates[monthKey] = 0f // Pre-fill with zeroes
                    tempCalendar.add(Calendar.MONTH, 1)
                }

                response.buckets.flatMap { it.dataSets }.flatMap { it.dataPoints }.forEach { dataPoint ->
                    val date = Date(dataPoint.getStartTime(TimeUnit.MILLISECONDS))
                    val monthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(date)
                    val heartRate = dataPoint.getValue(Field.FIELD_AVERAGE).asFloat()

                    // Update only if there's a heart rate value greater than 0
                    if (heartRate > 0) {
                        val currentAverage = monthlyHeartRates[monthKey] ?: 0f
                        val currentCount = if (currentAverage > 0) 1 else 0 // Assume each month's data starts with count 1 if average is already set
                        monthlyHeartRates[monthKey] = (currentAverage * currentCount + heartRate) / (currentCount + 1)
                    }
                }

                listener.onHeartRateDataReceived(monthlyHeartRates)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitMonthlyRestingHR", "There was a problem reading the data.", e)
                listener.onError(e)
            }
    }


    //week days steps
    interface StepDataWeekListener {
        fun onStepDataReceived(stepCounts: List<Int>)
        fun onError(e: Exception)
    }

    fun readWeekStepData(listener: StepDataWeekListener) {
        // Calculate start of the week (7 days ago at 00:00:00.000)
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6) // Move back 6 days to cover a total of 7 days including today
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val stepCounts = response.buckets.map { bucket ->
                    if (bucket.dataSets.isNotEmpty()) {
                        bucket.dataSets.flatMap { it.dataPoints }.sumOf { it.getValue(Field.FIELD_STEPS).asInt() }
                    } else {
                        0 // Add 0 if there are no data points for this day
                    }
                } // Removed .reversed()

                listener.onStepDataReceived(stepCounts)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitWeek", "There was a problem reading the data.", e)
                listener.onError(e)
            }
    }

    interface StepDataMonthListener {
        fun onStepDataMonthReceived(stepCounts: Map<String, Float>)
        fun onError(e: Exception)
    }

    fun readAverageStepsForPast12Months(listener: StepDataMonthListener) {
        val endCalendar = Calendar.getInstance()
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.YEAR, -1) // Move back 12 months
            add(Calendar.MONTH, +1) // Subtract one month to effectively move back by 11 months
            set(Calendar.DAY_OF_MONTH, 1) // Start from the first day of that month
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val dailyStepsMap = mutableMapOf<String, Int>()

                // Initialize map with zeros for each day in the past 12 months
                val tempCalendar = Calendar.getInstance().apply { timeInMillis = startTime }
                while (tempCalendar.timeInMillis < endTime) {
                    val key = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(tempCalendar.time)
                    dailyStepsMap[key] = 0 // Initialize each day with 0 steps
                    tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                // Populate the map with actual data
                response.buckets.flatMap { it.dataSets }.flatMap { it.dataPoints }.forEach { dataPoint ->
                    val date = Date(dataPoint.getStartTime(TimeUnit.MILLISECONDS))
                    val key = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                    val steps = dataPoint.getValue(Field.FIELD_STEPS).asInt()
                    dailyStepsMap[key] = dailyStepsMap.getOrDefault(key, 0) + steps
                }

                // Aggregate daily steps into monthly averages, considering only days with data
                val monthlyAverages = dailyStepsMap.entries.groupBy {
                    it.key.substring(0, 7) // Group by year-month
                }.mapValues { (_, entries) ->
                    val totalSteps = entries.sumOf { it.value }
                    val daysWithData = entries.count { it.value > 0 }
                    if (daysWithData > 0) totalSteps.toFloat() / daysWithData else 0f // Average based on days with data
                }

                listener.onStepDataMonthReceived(monthlyAverages)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitAvgSteps12M", "There was a problem reading the data.", e)
                listener.onError(e)
            }
    }

    //****** sleep data for chart
    interface SleepDataWeekListener {
        fun onSleepDataReceived(sleepMinutesPerDay: List<Int>)
        fun onError(e: Exception)
    }
    fun readWeekSleepData(listener: SleepDataWeekListener) {
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6) // Cover a total of 7 days including today
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_SLEEP_SEGMENT)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                // Initialize a map to store sleep minutes for each day
                val sleepMinutesPerDayMap = mutableMapOf<String, Int>()

                // Initialize the map with 0 for every day in the week
                for (i in 0 until 7) {
                    val dayCalendar = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, -i)
                    }
                    val dayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dayCalendar.time)
                    sleepMinutesPerDayMap[dayKey] = 0 // Initialize each day with 0 minutes
                }

                response.dataSets.flatMap { it.dataPoints }.forEach { dataPoint ->
                    val startTimePoint = dataPoint.getStartTime(TimeUnit.MILLISECONDS)
                    val endTimePoint = dataPoint.getEndTime(TimeUnit.MILLISECONDS)

                    val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(endTimePoint - startTimePoint).toInt()
                    if (durationMinutes <= 0) {
                        return@forEach // Skip if duration is not positive
                    }

                    val startCalendar = Calendar.getInstance().apply {
                        timeInMillis = startTimePoint
                    }
                    val endCalendar = Calendar.getInstance().apply {
                        timeInMillis = endTimePoint
                    }

                    // Ensure that the sleep duration is distributed correctly across days
                    while (startCalendar.before(endCalendar)) {
                        val dayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(startCalendar.time)
                        val endOfDay = startCalendar.clone() as Calendar
                        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
                        endOfDay.set(Calendar.MINUTE, 59)
                        endOfDay.set(Calendar.SECOND, 59)
                        endOfDay.set(Calendar.MILLISECOND, 999)

                        val sleepEnd = if (endCalendar.before(endOfDay)) endCalendar else endOfDay
                        val dailySleepDuration = TimeUnit.MILLISECONDS.toMinutes(sleepEnd.timeInMillis - startCalendar.timeInMillis).toInt()

                        sleepMinutesPerDayMap[dayKey] = sleepMinutesPerDayMap.getOrDefault(dayKey, 0) + dailySleepDuration

                        // Prepare for the next day
                        startCalendar.add(Calendar.DAY_OF_YEAR, 1)
                        startCalendar.set(Calendar.HOUR_OF_DAY, 0)
                        startCalendar.set(Calendar.MINUTE, 0)
                        startCalendar.set(Calendar.SECOND, 0)
                        startCalendar.set(Calendar.MILLISECOND, 0)
                    }
                }

                // Convert the map to a list ordered by date
                val sleepMinutesPerDay = sleepMinutesPerDayMap.entries.sortedBy { it.key }.map { it.value }
                listener.onSleepDataReceived(sleepMinutesPerDay)
            }
            .addOnFailureListener { e ->
                listener.onError(e)
            }
    }
    interface SleepDataMonthListener {
        fun onSleepDataMonthReceived(sleepCounts: Map<String, Float>)
        fun onError(e: Exception)
    }
    fun readAverageSleepForPast12Months(listener: SleepDataMonthListener) {
        val endCalendar = Calendar.getInstance()
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.YEAR, -1) // Move back 12 months
            add(Calendar.MONTH, +1) // Subtract one month to effectively move back by 11 months
            set(Calendar.DAY_OF_MONTH, 1) // Start from the first day of that month
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_SLEEP_SEGMENT)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val dailySleepMap = mutableMapOf<String, Int>()

                // Initialize map with zeros for each day in the past 12 months to account for days with no data
                val tempCalendar = Calendar.getInstance().apply { timeInMillis = startTime }
                while (tempCalendar.timeInMillis <= endTime) {
                    val key = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(tempCalendar.time)
                    dailySleepMap[key] = 0 // Initialize each day with 0 minutes
                    tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                // Populate the map with actual sleep data
                response.dataSets.flatMap { it.dataPoints }.forEach { dataPoint ->
                    val date = Date(dataPoint.getStartTime(TimeUnit.MILLISECONDS))
                    val key = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                    val sleepDurationMinutes = TimeUnit.MILLISECONDS.toMinutes(dataPoint.getEndTime(TimeUnit.MILLISECONDS) - dataPoint.getStartTime(TimeUnit.MILLISECONDS)).toInt()
                    dailySleepMap[key] = dailySleepMap.getOrDefault(key, 0) + sleepDurationMinutes
                }

                // Convert daily totals into monthly averages, correctly accounting for all days in each month
                val monthlyAverages = dailySleepMap.entries.groupBy {
                    it.key.substring(0, 7) // Group by year-month
                }.map { (month, entries) ->
                    val totalSleepMinutes = entries.sumOf { it.value }
                    val daysWithData = entries.count { it.value > 0 }
                    val averageSleepMinutes = if (daysWithData > 0) totalSleepMinutes.toFloat() / daysWithData else 0f
                    month to averageSleepMinutes
                }.toMap()

                listener.onSleepDataMonthReceived(monthlyAverages)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitAvgSleep12M", "There was a problem reading the sleep data.", e)
                listener.onError(e)
            }
    }

    //********BPM CHART DATA***********
    interface CaloriesDataWeekListener {
        fun onCaloriesDataReceived(calorieCounts: List<Float>)
        fun onError(e: Exception)
    }
    fun readWeekCaloriesData(listener: CaloriesDataWeekListener) {
        // Calculate the start of the week (7 days ago at 00:00:00.000)
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6) // Move back 6 days to cover a total of 7 days including today
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val calorieCounts = MutableList(7) { 0.0f } // Initialize with 0 for each day of the week

                response.buckets.forEachIndexed { index, bucket ->
                    val dataSet = bucket.dataSets.firstOrNull()
                    if (dataSet != null && dataSet.dataPoints.isNotEmpty()) {
                        val totalCalories = dataSet.dataPoints.sumOf { it.getValue(Field.FIELD_CALORIES).asFloat().toDouble() }.toFloat()
                        calorieCounts[index] = totalCalories
                    }
                }

                listener.onCaloriesDataReceived(calorieCounts)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitCaloriesWeek", "There was a problem reading the data.", e)
                listener.onError(e)
            }
    }

    interface HeartRateDataWeekListener {
        fun onHeartRateDataReceived(heartRateCounts: List<Float>)
        fun onError(e: Exception)
    }

    fun readWeekHeartRateData(listener: HeartRateDataWeekListener) {
        // Calculate start of the week (7 days ago at 00:00:00.000)
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6) // Move back 6 days to cover a total of 7 days including today
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_HEART_RATE_BPM)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val tempCalendar = Calendar.getInstance().apply { timeInMillis = startTime }
                val heartRateCounts = mutableListOf<Float>()

                // Initialize the list with 0 for each day of the week
                for (i in 1..7) {
                    heartRateCounts.add(0.0f)
                }

                response.buckets.forEachIndexed { index, bucket ->
                    val dataSet = bucket.dataSets.firstOrNull()
                    if (dataSet != null && dataSet.dataPoints.isNotEmpty()) {
                        // Assuming you want the average heart rate; adjust as necessary for max or min
                        val averageHeartRate = dataSet.dataPoints.map { it.getValue(Field.FIELD_AVERAGE).asFloat() }.average().toFloat()
                        heartRateCounts[index] = averageHeartRate
                    }
                    tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                listener.onHeartRateDataReceived(heartRateCounts)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitHeartRateWeek", "There was a problem reading the data.", e)
                listener.onError(e)
            }
    }
    interface HeartRateDataMonthListener {
        fun onHeartRateDataReceived(heartRateAverages: Map<String, Float>)
        fun onError(e: Exception)
    }
    fun readAverageHeartRateForPast12Months(listener: HeartRateDataMonthListener) {
        val endCalendar = Calendar.getInstance()
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.YEAR, -1) // Move back 12 months
            add(Calendar.MONTH, +1) // Subtract one month to effectively move back by 11 months
            set(Calendar.DAY_OF_MONTH, 1) // Start from the first day of that month
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val monthlyHeartRates = mutableMapOf<String, MutableList<Float>>()

                // Pre-fill the map with zeroes for every day of each month
                val calendarIterator = Calendar.getInstance().apply { timeInMillis = startTime }
                while (calendarIterator.before(endCalendar)) {
                    val monthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendarIterator.time)
                    val daysInMonth = calendarIterator.getActualMaximum(Calendar.DAY_OF_MONTH)

                    if (!monthlyHeartRates.containsKey(monthKey)) {
                        monthlyHeartRates[monthKey] = MutableList(daysInMonth) { 0f }
                    }

                    calendarIterator.add(Calendar.MONTH, 1)
                }

                // Populate the map with actual data
                response.buckets.flatMap { it.dataSets }.flatMap { it.dataPoints }.forEach { dataPoint ->
                    val date = Date(dataPoint.getStartTime(TimeUnit.MILLISECONDS))
                    val calendarForDataPoint = Calendar.getInstance().apply { time = date }
                    val monthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(date)
                    val dayOfMonth = calendarForDataPoint.get(Calendar.DAY_OF_MONTH) - 1

                    val averageHeartRate = dataPoint.getValue(Field.FIELD_AVERAGE).asFloat()
                    monthlyHeartRates[monthKey]?.set(dayOfMonth, averageHeartRate)
                }

                // Calculate monthly averages
                val monthlyAverageHeartRates = monthlyHeartRates.mapValues { (_, values) ->
                    if (values.all { it == 0f }) 0f // Check if all values are 0 or list is empty
                    else values.filter { it > 0 }.average().toFloat() // Calculate average excluding 0 values
                }

                listener.onHeartRateDataReceived(monthlyAverageHeartRates)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitAvgHeartRate12M", "There was a problem reading the data.", e)
                listener.onError(e)
            }
    }
    //********CALORIES CHART DATA ******************************
    interface CaloriesDataMonthListener {
        fun onCaloriesDataReceived(calorieAverages: Map<String, Float>)
        fun onError(e: Exception)
    }

    fun readAverageCaloriesForPast12Months(listener: CaloriesDataMonthListener) {
        val endCalendar = Calendar.getInstance()
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.YEAR, -1) // Move back 12 months
            add(Calendar.MONTH, +1) // Subtract one month to effectively move back by 11 months
            set(Calendar.DAY_OF_MONTH, 1) // Start from the first day of that month
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val monthlyCalories = mutableMapOf<String, MutableList<Float>>()

                // Pre-fill the map with zeroes for every day of each month
                val calendarIterator = Calendar.getInstance().apply { timeInMillis = startTime }
                while (calendarIterator.before(endCalendar)) {
                    val monthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendarIterator.time)
                    val daysInMonth = calendarIterator.getActualMaximum(Calendar.DAY_OF_MONTH)

                    if (!monthlyCalories.containsKey(monthKey)) {
                        monthlyCalories[monthKey] = MutableList(daysInMonth) { 0f }
                    }

                    calendarIterator.add(Calendar.MONTH, 1)
                }

                // Populate the map with actual data
                response.buckets.flatMap { it.dataSets }.flatMap { it.dataPoints }.forEach { dataPoint ->
                    val date = Date(dataPoint.getStartTime(TimeUnit.MILLISECONDS))
                    val calendarForDataPoint = Calendar.getInstance().apply { time = date }
                    val monthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(date)
                    val dayOfMonth = calendarForDataPoint.get(Calendar.DAY_OF_MONTH) - 1

                    val calories = dataPoint.getValue(Field.FIELD_CALORIES).asFloat()
                    monthlyCalories[monthKey]?.set(dayOfMonth, calories)
                }

                // Calculate monthly averages
                val monthlyAverageCalories = monthlyCalories.mapValues { (_, values) ->
                    if (values.all { it == 0f }) 0f
                    else values.filter { it > 0 }.average().toFloat()
                }

                listener.onCaloriesDataReceived(monthlyAverageCalories)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitAvgCalories12M", "There was a problem reading the data.", e)
                listener.onError(e)
            }
    }

    //***********BPM MAX AND MIN FOR WEEKDAYS
    interface HeartRateMinMaxDataWeekListener {
        fun onHeartRateDataReceived(heartRateData: List<Triple<String, Float, Float>>)
        fun onError(e: Exception)
    }


    fun readWeekHeartRateData(listener: HeartRateMinMaxDataWeekListener) {
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = endCalendar.timeInMillis

        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -6)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCalendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_HEART_RATE_BPM)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                val heartRateDataWithDates = mutableListOf<Triple<String, Float, Float>>()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val calendar = Calendar.getInstance()

                response.buckets.reversed().forEach { bucket ->
                    calendar.timeInMillis = bucket.getStartTime(TimeUnit.MILLISECONDS)
                    val dateStr = dateFormat.format(calendar.time)
                    val dataSet = bucket.dataSets.firstOrNull()

                    if (dataSet != null && dataSet.dataPoints.isNotEmpty()) {
                        val heartRates = dataSet.dataPoints.flatMap { dp ->
                            dp.dataType.fields.map { field ->
                                dp.getValue(field).asFloat()
                            }
                        }
                        val minHeartRate = heartRates.minOrNull() ?: 0.0f
                        val maxHeartRate = heartRates.maxOrNull() ?: 0.0f
                        heartRateDataWithDates.add(Triple(dateStr, minHeartRate, maxHeartRate))
                    } else {
                        heartRateDataWithDates.add(Triple(dateStr, 0.0f, 0.0f))
                    }
                }

                listener.onHeartRateDataReceived(heartRateDataWithDates)
            }
            .addOnFailureListener { e ->
                listener.onError(e)
            }
    }

    //DATA INSERTION



}
fun addCaloriesToGoogleFit(context: Context, calories: Float, startTime: Long, endTime: Long) {
    val dataSource = DataSource.Builder()
        .setAppPackageName(context)
        .setDataType(DataType.TYPE_NUTRITION)
        .setType(DataSource.TYPE_RAW)
        .build()

    val nutrientsMap = mapOf(Field.NUTRIENT_CALORIES to calories)

    // Create the data point with specific start and end times
    val dataPoint = DataPoint.builder(dataSource)
        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
        .setField(Field.FIELD_NUTRIENTS, nutrientsMap)
        .build()

    // Create the data set
    val dataSet = DataSet.builder(dataSource)
        .add(dataPoint)
        .build()

    // Insert the data set into Google Fit
    Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
        .insertData(dataSet)
        .addOnSuccessListener {
            // Data insert was successful
            Log.i("GoogleFitCalories", "Successfully added calories to Google Fit.")
        }
        .addOnFailureListener { e ->
            // Handle failure
            Log.e("GoogleFitCalories", "Failed to add calories to Google Fit.", e)
        }
}
fun disconnectFromGoogleFit(context: Context, fitnessOptions: FitnessOptions){
    // Disable Google Fit for the user.
    val configClient = Fitness.getConfigClient(context, GoogleSignIn.getAccountForExtension(context, fitnessOptions))
    configClient.disableFit()
        .addOnSuccessListener {
            Log.i(TAG, "Disabled Google Fit")
            // Proceed to sign out and revoke access.
            val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .addExtension(fitnessOptions)
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, signInOptions)

            // Sign out the user.
            googleSignInClient.signOut().addOnCompleteListener {
                Log.i(TAG, "User signed out from Google account")
            }

            // Revoke all granted permissions.
            googleSignInClient.revokeAccess().addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i(TAG, "Access to Google Fit revoked")
                } else {
                    Log.e(TAG, "Failed to revoke access", it.exception)
                }
            }
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "There was an error disabling Google Fit", e)
        }
}


