package iti.yousef.day2sharedflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
class SearchViewModel : ViewModel() {

    private val _searchQuery = MutableSharedFlow<String>(replay = 1)

    private val names = listOf("Ahmed", "ali", "Amr", "Mona", "mohamed", "Sara", "salma")

    val searchResults: StateFlow<List<String>> = _searchQuery.map { query ->
            if (query.isBlank()) emptyList()
            else names.filter { it.contains(query, ignoreCase = true) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearch(text: String) {
        viewModelScope.launch {
            _searchQuery.emit(text)
        }
    }
}