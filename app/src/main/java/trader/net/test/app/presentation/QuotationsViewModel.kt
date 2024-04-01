package trader.net.test.app.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import trader.net.test.app.R
import trader.net.test.app.domain.Quotation
import trader.net.test.app.domain.QuotationsRepository
import trader.net.test.app.domain.ResourceProvider
import trader.net.test.app.domain.TickersRepository

data class QuotationsViewState(
    val list: List<Quotation> = listOf(),
    val inProgress: Boolean = true,
    val error: String? = null
)

class QuotationsViewModel(
    private val tickersRepository: TickersRepository,
    private val quotationsRepository: QuotationsRepository,
    private val resourceProvider: ResourceProvider,
    coroutineDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _state = MutableStateFlow(QuotationsViewState())
    val state: StateFlow<QuotationsViewState> get() = _state

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(TAG, throwable.message.toString())
        _state.update {
            it.copy(
                error = resourceProvider.getString(R.string.error_something_wrong),
                inProgress = false
            )
        }
    }

    init {
        viewModelScope.launch(coroutineDispatcher + exceptionHandler) {
            quotationsRepository.getQuotations(tickersRepository.getTickers())
                .collect { list ->
                    _state.update { it.copy(error = null, inProgress = false, list = list) }
                }
        }
    }

    companion object {
        private const val TAG = "viewmodel"
    }
}
