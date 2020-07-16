package ru.skillbranch.gameofthrones.ui.splash

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.repositories.RootRepository
import ru.skillbranch.gameofthrones.utils.ui.base.BaseViewModel
import ru.skillbranch.gameofthrones.utils.ui.data.Event
import ru.skillbranch.gameofthrones.utils.ui.data.EventLiveData
import java.lang.Exception

class SplashViewModel(
    private val imageIds: List<Int>
) : BaseViewModel() {

    val animation = MutableLiveData<AnimationState?>()
    val showError = EventLiveData<Int>()
    var needNavigateToMain: Boolean = false
        private set

    private var lastImagePosition = 0
    private var isAnimationEnabled = true

    init {
        animation.value = AnimationState(R.drawable.spash, imageIds[lastImagePosition])
        loadData()
    }

    fun animationEnded() {
        if (isAnimationEnabled) {
            val nextImagePosition = when (lastImagePosition) {
                imageIds.size - 1 -> 0
                else -> lastImagePosition + 1
            }
            animation.value =
                AnimationState(imageIds[lastImagePosition], imageIds[nextImagePosition])
            lastImagePosition = nextImagePosition
        }
    }

    private fun loadData() {
        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val delayed = async {
                        delay(MIN_SPLASH_DURATION)
                    }
                    val loading = async {
                        with(RootRepository) {
                            val isNeedUpdate = isNeedUpdate()
                            if (isNeedUpdate) {
                                loadDataAndInsertToDB()
                            }
                        }
                    }
                    delayed.await()
                    loading.await()
                }
                needNavigateToMain = true
            } catch (e: Exception) {
                showError.postValue(Event(R.string.splash_no_connection_error))
                animation.postValue(null)
                isAnimationEnabled = false
            }
        }
    }

    data class AnimationState(
        val animatingImageId: Int,
        val backgroundImageId: Int,
        val duration: Long = ANIMATION_DURATION
    )

    companion object {
        private const val ANIMATION_DURATION = 2000L
        private const val MIN_SPLASH_DURATION = 5000L
    }
}