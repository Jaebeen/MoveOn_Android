package com.example.moveon.ui.guide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GuideViewModel : ViewModel() {
    private val _currentPage = MutableLiveData(0)
    val currentPage: LiveData<Int> = _currentPage

    private val _isLastPage = MutableLiveData(false)
    val isLastPage: LiveData<Boolean> = _isLastPage

    private val _event = MutableLiveData<GuideEvent?>()
    val event: LiveData<GuideEvent?> = _event

    fun setCurrentPage(position: Int) {
        val page = position.coerceIn(0, LAST_PAGE_INDEX)
        _currentPage.value = page
        _isLastPage.value = page == LAST_PAGE_INDEX
    }

    fun onPageSelected(position: Int, hasRequiredPermissions: Boolean) {
        if (position == LAST_PAGE_INDEX && !hasRequiredPermissions) {
            _event.value = GuideEvent.RequestPermission(forceRequest = false)
            return
        }

        setCurrentPage(position)

        if (position == PERMISSION_PAGE_INDEX && !hasRequiredPermissions) {
            _event.value = GuideEvent.RequestPermission(forceRequest = false)
        }
    }

    fun onNextClicked(currentPage: Int, hasRequiredPermissions: Boolean) {
        when {
            currentPage >= LAST_PAGE_INDEX -> {
                _event.value = GuideEvent.FinishGuide
            }

            currentPage == PERMISSION_PAGE_INDEX && !hasRequiredPermissions -> {
                _event.value = GuideEvent.RequestPermission(forceRequest = true)
            }

            else -> {
                _event.value = GuideEvent.MoveToPage(currentPage + 1, smoothScroll = true)
            }
        }
    }

    fun onPermissionResult(hasRequiredPermissions: Boolean) {
        if (hasRequiredPermissions) {
            _event.value = GuideEvent.MoveToPage(LAST_PAGE_INDEX, smoothScroll = true)
        }
    }

    fun clearEvent() {
        _event.value = null
    }

    companion object {
        const val PAGE_COUNT = 4
        const val PERMISSION_PAGE_INDEX = 2
        const val LAST_PAGE_INDEX = PAGE_COUNT - 1
    }
}

sealed class GuideEvent {
    data class MoveToPage(
        val position: Int,
        val smoothScroll: Boolean
    ) : GuideEvent()

    data class RequestPermission(
        val forceRequest: Boolean
    ) : GuideEvent()

    data object FinishGuide : GuideEvent()
}
