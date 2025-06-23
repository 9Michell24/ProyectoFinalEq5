package mx.edu.potros.gestioninventarios.ui.add_item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddItemViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is an Add Item Fragment"
    }
    val text: LiveData<String> = _text
}