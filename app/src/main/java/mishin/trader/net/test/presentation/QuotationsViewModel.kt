package mishin.trader.net.test.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mishin.trader.net.test.data.network.rest.entity.QuotationRestData
import mishin.trader.net.test.data.repository.TickersRepository
import mishin.trader.net.test.domain.QuotationsRepository

data class QuotationsViewState(
    val array: List<QuotationRestData> = listOf(),
    val inProgress: Boolean = false,
    val error: String? = null
)

class QuotationsViewModel(
    private val tickersRepository: TickersRepository,
    private val quotationsRepository: QuotationsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(QuotationsViewState())
    val state: StateFlow<QuotationsViewState> get() = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            quotationsRepository.getQuotations(tickersRepository.getTickers())
                /* .handleErrors { error ->
                     _state.update { it.copy(error = error.message) }
                 }*/
                .map {
                    // TODO: map to ui model
                }
                .collect {
                    _state.update { it.copy(error = null) }
                }
        }
    }
}
