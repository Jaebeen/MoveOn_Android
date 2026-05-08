package com.example.moveon.ui.guide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GuideViewModel : ViewModel() {
    private val _currentPage = MutableLiveData(0)
    val currentPage: LiveData<Int> = _currentPage

    private val _isLastPage = MutableLiveData(false)
    val isLastPage: LiveData<Boolean> = _isLastPage

    fun setCurrentPage(position: Int) {
        val page = position.coerceIn(0, LAST_PAGE_INDEX)
        _currentPage.value = page
        _isLastPage.value = page == LAST_PAGE_INDEX
    }

    fun nextPage(currentPage: Int): Int? {
        return if (currentPage >= LAST_PAGE_INDEX) {
            null
        } else {
            currentPage + 1
        }
    }

    companion object {
        const val PAGE_COUNT = 4
        const val PERMISSION_PAGE_INDEX = 2
        const val LAST_PAGE_INDEX = PAGE_COUNT - 1
    }
}
