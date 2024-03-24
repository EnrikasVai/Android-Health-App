package com.example.bakis

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.data.Field
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class GoogleFitDataHandler(private val context: Context) {


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
                Toast.makeText(context, "G-FIT Total steps: $totalSteps", Toast.LENGTH_LONG).show()
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
                Toast.makeText(context, "G-FIT Total sleep: $totalSleepMinutes minutes", Toast.LENGTH_LONG).show()
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
                Toast.makeText(context, "G-FIT Total sleep: $totalCalories cal", Toast.LENGTH_LONG).show()
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
                Toast.makeText(context, "G-FIT Average Heart Rate: $averageHeartRate bpm", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitHeartRate", "There was a problem reading the heart rate data.", e)
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
                }.mapValues { (month, entries) ->
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
                    val sleepDuration = TimeUnit.MILLISECONDS.toMinutes(endTimePoint - startTimePoint).toInt()

                    val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(startTimePoint))
                    sleepMinutesPerDayMap[dateKey] = sleepMinutesPerDayMap.getOrDefault(dateKey, 0) + sleepDuration
                }

                // Convert the map to a list ordered by date
                val sleepMinutesPerDay = sleepMinutesPerDayMap.entries.sortedBy { it.key }.map { it.value }
                listener.onSleepDataReceived(sleepMinutesPerDay)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleFitSleepWeek", "There was a problem reading the sleep data.", e)
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











}

