package com.example.personaleventplanner.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.personaleventplanner.R
import com.example.personaleventplanner.data.Event
import com.example.personaleventplanner.databinding.FragmentAddEventBinding
import com.example.personaleventplanner.viewmodel.EventViewModel
import java.util.Calendar

class AddEventFragment : Fragment(R.layout.fragment_add_event) {

    private var _binding: FragmentAddEventBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by activityViewModels()
    private val selectedCalendar: Calendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddEventBinding.bind(view)

        val categories = listOf("Work", "Social", "Travel", "Personal")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        binding.btnPickDate.setOnClickListener { pickDate() }
        binding.btnPickTime.setOnClickListener { pickTime() }
        binding.btnSaveEvent.setOnClickListener { saveEvent() }
    }

    private fun pickDate() {
        val now = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedCalendar.set(year, month, dayOfMonth)
                binding.tvSelectedDate.text =
                    String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun pickTime() {
        val now = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minute)
                selectedCalendar.set(Calendar.SECOND, 0)
                selectedCalendar.set(Calendar.MILLISECOND, 0)

                binding.tvSelectedTime.text =
                    String.format("%02d:%02d", hourOfDay, minute)
            },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun saveEvent() {
        val title = binding.etTitle.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()
        val location = binding.etLocation.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.tvSelectedDate.text.toString() == "No date selected") {
            Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.tvSelectedTime.text.toString() == "No time selected") {
            Toast.makeText(requireContext(), "Please select a time", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCalendar.timeInMillis < System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "Please select a future date and time", Toast.LENGTH_SHORT).show()
            return
        }

        val event = Event(
            title = title,
            category = category,
            location = location,
            dateTime = selectedCalendar.timeInMillis
        )

        eventViewModel.insert(event)
        Toast.makeText(requireContext(), "Event added successfully", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.eventListFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}