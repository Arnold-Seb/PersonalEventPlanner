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
import androidx.navigation.fragment.navArgs
import com.example.personaleventplanner.R
import com.example.personaleventplanner.data.Event
import com.example.personaleventplanner.databinding.FragmentEditEventBinding
import com.example.personaleventplanner.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditEventFragment : Fragment(R.layout.fragment_edit_event) {

    private var _binding: FragmentEditEventBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by activityViewModels()
    private val args: EditEventFragmentArgs by navArgs()

    private var currentEvent: Event? = null
    private val selectedCalendar: Calendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditEventBinding.bind(view)

        val categories = listOf("Work", "Social", "Travel", "Personal")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoryEdit.adapter = adapter

        eventViewModel.getEventById(args.eventId) { event ->
            event?.let {
                currentEvent = it
                selectedCalendar.timeInMillis = it.dateTime

                binding.etTitleEdit.setText(it.title)
                binding.etLocationEdit.setText(it.location)

                val categoryPosition = categories.indexOf(it.category)
                if (categoryPosition >= 0) {
                    binding.spinnerCategoryEdit.setSelection(categoryPosition)
                }

                updateDateTimeLabels()
            }
        }

        binding.btnPickDateEdit.setOnClickListener { pickDate() }
        binding.btnPickTimeEdit.setOnClickListener { pickTime() }
        binding.btnUpdateEvent.setOnClickListener { updateEvent() }
    }

    private fun pickDate() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedCalendar.set(year, month, dayOfMonth)
                updateDateTimeLabels()
            },
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun pickTime() {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minute)
                selectedCalendar.set(Calendar.SECOND, 0)
                selectedCalendar.set(Calendar.MILLISECOND, 0)
                updateDateTimeLabels()
            },
            selectedCalendar.get(Calendar.HOUR_OF_DAY),
            selectedCalendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun updateDateTimeLabels() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        binding.tvSelectedDateEdit.text = dateFormat.format(selectedCalendar.time)
        binding.tvSelectedTimeEdit.text = timeFormat.format(selectedCalendar.time)
    }

    private fun updateEvent() {
        val title = binding.etTitleEdit.text.toString().trim()
        val category = binding.spinnerCategoryEdit.selectedItem.toString()
        val location = binding.etLocationEdit.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCalendar.timeInMillis < System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "Please select a future date and time", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedEvent = currentEvent?.copy(
            title = title,
            category = category,
            location = location,
            dateTime = selectedCalendar.timeInMillis
        )

        if (updatedEvent != null) {
            eventViewModel.update(updatedEvent)
            Toast.makeText(requireContext(), "Event updated successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.eventListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}