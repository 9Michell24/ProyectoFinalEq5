package mx.edu.potros.gestioninventarios.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is a home Fragment"
    }
    val text: LiveData<String> = _text
}