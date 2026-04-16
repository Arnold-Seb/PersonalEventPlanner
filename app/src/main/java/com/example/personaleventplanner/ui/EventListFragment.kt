package com.example.personaleventplanner.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.personaleventplanner.R
import com.example.personaleventplanner.databinding.FragmentEventListBinding
import com.example.personaleventplanner.ui.adapter.EventAdapter
import com.example.personaleventplanner.viewmodel.EventViewModel
import com.google.android.material.snackbar.Snackbar

class EventListFragment : Fragment(R.layout.fragment_event_list) {

    private var _binding: FragmentEventListBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by activityViewModels()
    private lateinit var eventAdapter: EventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEventListBinding.bind(view)

        eventAdapter = EventAdapter(
            onItemClick = { event ->
                val action = EventListFragmentDirections
                    .actionEventListFragmentToEditEventFragment(event.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { event ->
                eventViewModel.delete(event)
                Snackbar.make(binding.root, "Event deleted successfully", Snackbar.LENGTH_SHORT).show()
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }

        eventViewModel.allEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
            binding.tvEmpty.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.addEventFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}