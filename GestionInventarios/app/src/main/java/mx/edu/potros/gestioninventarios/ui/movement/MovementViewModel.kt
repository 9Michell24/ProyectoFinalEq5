package mx.edu.potros.gestioninventarios.ui.movement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MovementViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is a movement Fragment"
    }
    val text: LiveData<String> = _text
}