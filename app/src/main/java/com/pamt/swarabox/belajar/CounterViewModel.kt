package com.pamt.swarabox.belajar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class CounterViewModel (
    private val repository: CounterRepository = CounterRepository()
) : ViewModel() {

    val angka: StateFlow<Int> = repository.angka

    fun tambahAngka() {
        repository.tambahAngka()
    }
}