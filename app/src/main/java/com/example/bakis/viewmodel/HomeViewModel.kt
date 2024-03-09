package com.example.bakis.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakis.database.Repository
import com.example.bakis.database.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val _studentDetailsList = MutableStateFlow(emptyList<UserEntity>())
    val userDetailsList = _studentDetailsList.asStateFlow()

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
                }
            }
        }
    }

}

