package com.example.bakis.viewmodel


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakis.GoogleFitDataHandler
import com.example.bakis.database.Repository
import com.example.bakis.database.UserEntity
import com.example.bakis.database.WaterIntakeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationContext private val context: Context,
) : ViewModel() {


    private val _stepCount = MutableStateFlow("0")
    val stepCount = _stepCount.asStateFlow()
    private val _sleepCount = MutableStateFlow("0")
    val sleepCount = _sleepCount.asStateFlow()
    private val _calCount = MutableStateFlow("0")
    val calCount = _calCount.asStateFlow()
    private val _bpmCount = MutableStateFlow("0")
    val bpmCount = _bpmCount.asStateFlow()
    private val _todayDistance = MutableStateFlow(0.0)
    val todayDistance = _todayDistance.asStateFlow()
    private val _todayMoveMinutes = MutableStateFlow(0.0)
    val todayMoveMinutes = _todayMoveMinutes.asStateFlow()
    private val _todayAverageSpeed = MutableStateFlow(0.0)
    val todayAverageSpeed = _todayAverageSpeed.asStateFlow()


    private val _weeklyStepCounts = MutableStateFlow<List<Int>>(emptyList())
    val weeklyStepCounts = _weeklyStepCounts.asStateFlow()
    private val _monthlyStepCounts = MutableStateFlow<List<Float>>(emptyList())
    val monthlyStepCounts: StateFlow<List<Float>> = _monthlyStepCounts.asStateFlow()

    private val _weeklySleepCounts = MutableStateFlow<List<Int>>(emptyList())
    val weeklySleepCounts: StateFlow<List<Int>> = _weeklySleepCounts.asStateFlow()
    private val _monthlySleepCounts = MutableStateFlow<List<Float>>(emptyList())
    val monthlySleepCounts: StateFlow<List<Float>> = _monthlySleepCounts.asStateFlow()

    private val _weeklyHeartRateCounts = MutableStateFlow<List<Float>>(emptyList())
    val weeklyHeartRateCounts: StateFlow<List<Float>> = _weeklyHeartRateCounts.asStateFlow()
    private val _monthlyHeartRateCounts = MutableStateFlow<List<Float>>(emptyList())
    val monthlyHeartRateCounts: StateFlow<List<Float>> = _monthlyHeartRateCounts.asStateFlow()

    private val _weeklyCaloriesCounts = MutableStateFlow<List<Float>>(emptyList())
    val weeklyCaloriesCounts: StateFlow<List<Float>> = _weeklyCaloriesCounts.asStateFlow()
    private val _monthlyCaloriesCounts = MutableStateFlow<List<Float>>(emptyList())
    val monthlyCaloriesCounts: StateFlow<List<Float>> = _monthlyCaloriesCounts.asStateFlow()

    private val _weeklyHeartRateCountsMinMax = MutableStateFlow<List<Triple<String, Float, Float>>>(emptyList())
    val weeklyHeartRateCountsMinMax: StateFlow<List<Triple<String, Float, Float>>> = _weeklyHeartRateCountsMinMax.asStateFlow()

    init {
        fetchStepCount()
        fetchSleepCount()
        fetchCalCount()
        fetchBpmCount()
        fetchWeeklyStepCount()
        fetchMonthlyStepCounts()
        fetchWeeklySleepCount()
        fetchMonthlySleepCounts()
        fetchWeeklyHeartRateCount()
        fetchMonthlyHeartRateCounts()
        fetchWeeklyCaloriesCount()
        fetchMonthlyCaloriesCounts()
        fetchWeeklyHeartRateMinMax()
        fetchFitnessData()
    }
    fun fetchFitnessData() {
        val googleFitDataHandler = GoogleFitDataHandler(context)

        googleFitDataHandler.readFitnessData(object : GoogleFitDataHandler.TodayDataListener {
            override fun onStepDataReceived(distance: Double, moveMinutes: Double, averageSpeed: Double) {
                viewModelScope.launch {
                    _todayDistance.value = distance
                    _todayMoveMinutes.value = moveMinutes
                    _todayAverageSpeed.value = averageSpeed
                }
            }
            override fun onError(e: Exception) {
                Log.e("FitnessViewModel", "Error fetching fitness data", e)
            }
        })
    }
    fun fetchBpmCount() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        googleFitDataHandler.readHeartRateData(object : GoogleFitDataHandler.HeartRateDataListener {
            override fun onHeartRateDataReceived(bpmCount: Float) {
                _bpmCount.value = bpmCount.toString()
            }

            override fun onError(e: Exception) {
                Log.e("HomeViewModel", "Error fetching step count", e)
            }
        })
    }
    fun fetchStepCount() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        googleFitDataHandler.readStepData(object : GoogleFitDataHandler.StepDataListener {
            override fun onStepDataReceived(stepCount: Int) {
                _stepCount.value = stepCount.toString()
            }

            override fun onError(e: Exception) {
                Log.e("HomeViewModel", "Error fetching step count", e)
            }
        })
    }

    fun fetchSleepCount() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        googleFitDataHandler.readSleepData(object : GoogleFitDataHandler.SleepDataListener {
            override fun onSleepDataReceived(sleepCount: Int) {
                _sleepCount.value = sleepCount.toString()
            }

            override fun onError(e: Exception) {
                Log.e("HomeViewModel", "Error fetching sleep count", e)
            }
        })
    }
    fun fetchCalCount() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        googleFitDataHandler.readCaloriesData(object : GoogleFitDataHandler.CaloriesDataListener {
            override fun onCalDataReceived(calCount: Int) {
                _calCount.value = calCount.toString()
            }

            override fun onError(e: Exception) {
                Log.e("HomeViewModel", "Error fetching calorie count", e)
            }
        })
    }
    fun fetchWeeklyStepCount() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        googleFitDataHandler.readWeekStepData(object : GoogleFitDataHandler.StepDataWeekListener {
            override fun onStepDataReceived(stepCounts: List<Int>) {
                _weeklyStepCounts.value = stepCounts
            }

            override fun onError(e: Exception) {
                Log.e("HomeViewModel", "Error fetching weekly step count", e)
            }
        })
    }
    fun fetchMonthlyStepCounts() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        googleFitDataHandler.readAverageStepsForPast12Months(object : GoogleFitDataHandler.StepDataMonthListener {
            override fun onStepDataMonthReceived(stepCounts: Map<String, Float>) { // Ensure this matches interface
                _monthlyStepCounts.value = stepCounts.values.toList() // This already expects Float, matching the interface
            }

            override fun onError(e: Exception) {
                Log.e("ViewModel", "Error fetching monthly step counts", e)
            }
        })
    }
    fun fetchWeeklySleepCount() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        googleFitDataHandler.readWeekSleepData(object : GoogleFitDataHandler.SleepDataWeekListener {
            override fun onSleepDataReceived(sleepMinutesPerDay: List<Int>) {
                _weeklySleepCounts.value = sleepMinutesPerDay
            }

            override fun onError(e: Exception) {
                Log.e("HomeViewModel", "Error fetching weekly sleep count", e)
            }
        })
    }
    fun fetchMonthlySleepCounts() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        googleFitDataHandler.readAverageSleepForPast12Months(object : GoogleFitDataHandler.SleepDataMonthListener {
            override fun onSleepDataMonthReceived(sleepCount: Map<String, Float>) { // Ensure this matches interface
                _monthlySleepCounts.value = sleepCount.values.toList() // This already expects Float, matching the interface
            }

            override fun onError(e: Exception) {
                Log.e("ViewModel", "Error fetching monthly step counts", e)
            }
        })
    }
    fun fetchWeeklyHeartRateCount() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        googleFitDataHandler.readWeekHeartRateData(object : GoogleFitDataHandler.HeartRateDataWeekListener {
            override fun onHeartRateDataReceived(heartRateCounts: List<Float>) {
                _weeklyHeartRateCounts.value = heartRateCounts
            }

            override fun onError(e: Exception) {
                Log.e("HomeViewModel", "Error fetching weekly heart rate data", e)
            }
        })
    }
    fun fetchMonthlyHeartRateCounts() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        googleFitDataHandler.readAverageHeartRateForPast12Months(object : GoogleFitDataHandler.HeartRateDataMonthListener {
            override fun onHeartRateDataReceived(heartRateCounts: Map<String, Float>) { // Ensure this matches interface
                _monthlyHeartRateCounts.value = heartRateCounts.values.toList() // This already expects Float, matching the interface
            }

            override fun onError(e: Exception) {
                Log.e("ViewModel", "Error fetching monthly step counts", e)
                // Error handling remains the same
            }
        })
    }
    fun fetchWeeklyCaloriesCount() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        googleFitDataHandler.readWeekCaloriesData(object : GoogleFitDataHandler.CaloriesDataWeekListener {
            override fun onCaloriesDataReceived(caloriesCounts: List<Float>) {
                // Assuming _weeklyHeartRateCounts is a LiveData or similar observable data holder for the UI
                // This variable should be defined somewhere in your ViewModel or similar structure
                _weeklyCaloriesCounts.value = caloriesCounts
            }

            override fun onError(e: Exception) {
                Log.e("HomeViewModel", "Error fetching weekly heart rate data", e)
                // Handle error, maybe set _weeklyHeartRateCounts value to an empty list or some error indication
            }
        })
    }
    fun fetchMonthlyCaloriesCounts() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        googleFitDataHandler.readAverageCaloriesForPast12Months(object : GoogleFitDataHandler.CaloriesDataMonthListener {
            override fun onCaloriesDataReceived(caloriesCounts: Map<String, Float>) { // Ensure this matches interface
                _monthlyCaloriesCounts.value = caloriesCounts.values.toList() // This already expects Float, matching the interface
            }

            override fun onError(e: Exception) {
                Log.e("ViewModel", "Error fetching monthly step counts", e)
                // Error handling remains the same
            }
        })
    }
    fun fetchWeeklyHeartRateMinMax() {
        val googleFitDataHandler = GoogleFitDataHandler(context)
        viewModelScope.launch {
            googleFitDataHandler.readWeekHeartRateData(object :
                GoogleFitDataHandler.HeartRateMinMaxDataWeekListener {
                override fun onHeartRateDataReceived(heartRateData: List<Triple<String, Float, Float>>) {
                    _weeklyHeartRateCountsMinMax.value = heartRateData
                }
                override fun onError(e: Exception) {
                    Log.e("ViewModel", "Error fetching weekly heart rate data", e)
                }
            })
        }
    }







    private val _studentDetailsList = MutableStateFlow(emptyList<UserEntity>())
    val userDetailsList = _studentDetailsList.asStateFlow()

    private val _dailyWaterIntake = MutableStateFlow<List<WaterIntakeEntity>>(emptyList())
    val dailyWaterIntake: StateFlow<List<WaterIntakeEntity>> = _dailyWaterIntake.asStateFlow()

    private val _totalDailyIntake = MutableStateFlow(0)
    val totalDailyIntake: StateFlow<Int> = _totalDailyIntake.asStateFlow()

    init {
        // Call getUserDetails upon initialization to fetch user details from the database
        getUserDetails()
    }
    //Check if database has users for navigation
    private val _hasUsers = MutableStateFlow<Boolean?>(null) // Initially null to indicate not checked yet
    val hasUsers: StateFlow<Boolean?> = _hasUsers.asStateFlow()
    fun checkIfUsersExist() {
        viewModelScope.launch(IO) {
            repository.getAllUsers().collectLatest { users ->
                _hasUsers.tryEmit(users.isNotEmpty())
            }
        }
    }


    fun updateUser(userEntity: UserEntity) {
        viewModelScope.launch(IO) {
            repository.update(userEntity)
        }
    }

    fun insertUser(userEntity: UserEntity) {
        viewModelScope.launch(IO) {
            repository.insert(userEntity)
        }
    }
    fun deleteUser(userEntity: UserEntity){
        viewModelScope.launch(IO) {
            repository.delete(userEntity)
        }
    }


    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()
    fun setUserName(name: String) {
        _userName.tryEmit(name)
    }

    private val _userId = MutableStateFlow(0)
    val userId = _userId.asStateFlow()
    fun setUserId(id: Int) {
        _userId.tryEmit(id)
    }

    private val _userAge = MutableStateFlow(18)
    val userAge = _userAge.asStateFlow()
    fun setUserAge(age: Int) {
        _userAge.tryEmit(age)
    }

    private val _userWeight = MutableStateFlow(60.0)
    val userWeight = _userWeight.asStateFlow()
    fun setUserWeight(weight: Double) {
        _userWeight.tryEmit(weight)
    }

    private val _userHeight = MutableStateFlow(160)
    val userHeight = _userHeight.asStateFlow()
    fun setUserHeight(height: Int) {
        _userHeight.tryEmit(height)
    }

    private val _userSex = MutableStateFlow(false)
    val userSex = _userSex.asStateFlow()
    fun setUserSex(sex: Boolean) {
        _userSex.tryEmit(sex)
    }

    private val _userWaterGoal = MutableStateFlow(2000)
    val userWaterGoal = _userWaterGoal.asStateFlow()
    fun setUserWaterGoal(waterGoal: Int) {
        _userWaterGoal.tryEmit(waterGoal)
    }
    private val _userStepGoal = MutableStateFlow(5000)
    val userStepGoal = _userStepGoal.asStateFlow()
    fun setUserStepGoal(stepGoal: Int) {
        _userStepGoal.tryEmit(stepGoal)
    }

    //Delete current user
    fun deleteUserAll() {
        viewModelScope.launch(IO) {
            repository.deleteAllUsers()
        }
    }

    // USER DETAIL UPDATE******************************************
    fun updateUserName(userId: Int, newName: String) {
        viewModelScope.launch(IO) {
            repository.updateUserName(userId, newName)
            _userName.emit(newName) // Update the UI state to reflect the change
        }
    }
    fun updateUserAge(userId: Int, newAge: Int) {
        viewModelScope.launch(IO) {
            repository.updateUserAge(userId, newAge)
        }
    }
    fun updateUserWeight(userId: Int, newWeight: Double) {
        viewModelScope.launch(IO) {
            repository.updateUserWeight(userId, newWeight)
        }
    }
    fun updateUserHeight(userId: Int, newHeight: Int) {
        viewModelScope.launch(IO) {
            repository.updateUserHeight(userId, newHeight)
        }
    }
    fun updateUserSex(userId: Int, newSex: Boolean) {
        viewModelScope.launch(IO) {
            repository.updateUserSex(userId, newSex)
        }
    }
    fun updateUserWaterGoal(userId: Int, waterGoal: Int) {
        viewModelScope.launch(IO) {
            repository.updateUserWaterGoal(userId, waterGoal)
        }
    }
    fun updateUserStepGoal(userId: Int, stepGoal: Int) {
        viewModelScope.launch(IO) {
            repository.updateUserStepGoal(userId, stepGoal)
        }
    }
    private fun getUserDetails() {
        viewModelScope.launch(IO) {
            repository.getAllUsers().collectLatest { users ->
                // Assuming you're fetching a single user here
                if (users.isNotEmpty()) {
                    val user = users.first() // Assuming you're fetching the first user
                    _userName.emit(user.name)
                    _userAge.emit(user.age)
                    _userWeight.emit(user.weight)
                    _userHeight.emit(user.height)
                    _userSex.emit(user.sex)
                    _userId.emit(user.id)
                    _userWaterGoal.emit(user.waterGoal)
                    _userStepGoal.emit(user.stepGoal)
                }
            }
        }
    }
    //water intake
    fun addWaterIntake(userId: Int, intakeAmount: Int, date: String) {
        viewModelScope.launch {
            val waterIntakeEntity = WaterIntakeEntity(userId = userId, intakeAmount = intakeAmount, date = date)
            repository.insertWaterIntake(waterIntakeEntity)
            // Refresh daily intake
            fetchDailyWaterIntakeForUser(userId, date)
        }
    }
    // Assuming you have a function to fetch water intake records
    val waterIntakeRecords = MutableStateFlow<List<WaterIntakeEntity>>(emptyList())

    fun fetchWaterIntakeRecords(userId: Int) {
        viewModelScope.launch {
            repository.getAllWaterIntakesForUser(userId).collectLatest { records ->
                waterIntakeRecords.value = records
            }
        }
    }
    fun fetchDailyWaterIntakeForUser(userId: Int, date: String) {
        viewModelScope.launch(IO) {
            repository.getWaterIntakeForUserByDate(userId, date).collectLatest { intakeRecords ->
                _dailyWaterIntake.emit(intakeRecords)
                _totalDailyIntake.emit(intakeRecords.sumOf { it.intakeAmount })
            }
        }
    }

    fun deleteWaterIntake(waterIntakeEntity: WaterIntakeEntity) {
        viewModelScope.launch(IO) {
            repository.deleteWaterIntake(waterIntakeEntity)
            // Refresh daily intake
            fetchDailyWaterIntakeForUser(waterIntakeEntity.userId, waterIntakeEntity.date)
        }
    }
    // Goal management
    private val _intakeGoal = MutableStateFlow(2000) // Default goal
    val intakeGoal: StateFlow<Int> = _intakeGoal.asStateFlow()

    fun setIntakeGoal(newGoal: Int) {
        _intakeGoal.value = newGoal
    }
    //water graph logic
    fun getDailyWaterIntakeTotalsForUser(userId: Int): Flow<List<Pair<String, Int>>> {
        return repository.getAllWaterIntakesForUser(userId)
            .map { waterIntakes ->
                // Generate a map of the last 7 days with default intake values of 0
                val lastSevenDaysWithDefaults = (0..6).associate {
                    LocalDate.now().minusDays(it.toLong()).format(DateTimeFormatter.ISO_DATE) to 0
                }.toMutableMap()

                // Process water intakes from the database
                val processedIntakes = waterIntakes.filter {
                    val intakeDate = LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE)
                    intakeDate.isAfter(LocalDate.now().minusDays(7)) || intakeDate.isEqual(LocalDate.now())
                }.groupBy { it.date }
                    .mapValues { (_, intakes) -> intakes.sumOf { it.intakeAmount } }

                // Update the map with actual data, overriding defaults where applicable
                processedIntakes.forEach { (date, intake) ->
                    lastSevenDaysWithDefaults[date] = intake
                }

                // Convert the map to a list and sort it to have the dates in ascending order
                lastSevenDaysWithDefaults.toList().sortedBy { (date, _) ->
                    LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
                }
            }
    }
    fun getMonthlyWaterIntakeAveragesForUser(userId: Int): Flow<List<Pair<String, Double>>> {
        return repository.getAllWaterIntakesForUser(userId)
            .map { waterIntakes ->
                // Generate a list of the last 12 months including the current month.
                val lastTwelveMonths = (0L..11L).map {
                    YearMonth.now().minusMonths(it)
                }.reversed()

                // Filter and group the water intakes by month.
                val intakeAveragesByMonth = waterIntakes.filter {
                    val intakeDate = LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE)
                    YearMonth.from(intakeDate) in lastTwelveMonths
                }.groupBy {
                    val intakeDate = LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE)
                    YearMonth.from(intakeDate).toString()
                }.mapValues { (month, intakes) ->
                    // Calculate the number of days in the month.
                    val yearMonth = YearMonth.parse(month)
                    val daysInMonth = yearMonth.lengthOfMonth()

                    // Calculate the total intake for the month.
                    val totalIntake = intakes.sumOf { it.intakeAmount }

                    // Calculate the average intake amount for the month.
                    totalIntake.toDouble() / daysInMonth
                }

                // Ensure all months are represented, inserting an average of 0.0 for months without data.
                lastTwelveMonths.map { month ->
                    month.toString() to (intakeAveragesByMonth[month.toString()] ?: 0.0)
                }
            }
    }




}

