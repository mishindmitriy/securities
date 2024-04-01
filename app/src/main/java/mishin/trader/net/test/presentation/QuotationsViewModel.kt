package mishin.trader.net.test.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mishin.trader.net.test.data.network.quotations.QuotationRawData
import mishin.trader.net.test.domain.QuotationsRepository
import mishin.trader.net.test.domain.TickersRepository

data class QuotationsViewState(
    val array: List<QuotationRawData> = listOf(),
    val inProgress: Boolean = false,
    val error: String? = null
)

class QuotationsViewModel(
    private val tickersRepository: TickersRepository,
    private val quotationsRepository: QuotationsRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _state = MutableStateFlow(QuotationsViewState())
    val state: StateFlow<QuotationsViewState> get() = _state

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(TAG, throwable.message.toString())
        // TODO: error state
    }

    init {
        viewModelScope.launch(coroutineDispatcher + exceptionHandler) {
            quotationsRepository.getQuotations(tickersRepository.getTickers())
                /* .handleErrors { error ->
                     _state.update { it.copy(error = error.message) }
                 }*/
                .map {
                    // TODO: map to ui model
                }
                .collect {
                    // _state.update { it.copy(error = null) }
                }
        }
    }

    companion object {
        private const val TAG = "viewmodel"
    }
}
