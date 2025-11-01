package com.brett.coloringkids.ui

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.brett.coloringkids.R
import com.brett.coloringkids.databinding.FragmentDrawingBinding
import com.brett.coloringkids.domain.model.Tool
import com.brett.coloringkids.ui.adapters.ColorItem
import com.brett.coloringkids.ui.adapters.ColorPaletteAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DrawingFragment : Fragment() {

    private var _binding: FragmentDrawingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DrawingViewModel by viewModels()

    private lateinit var colorPaletteAdapter: ColorPaletteAdapter
    private var currentSelectedColor: Int = Color.BLACK

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            // Take persistable permission
            requireContext().contentResolver.takePersistableUriPermission(
                it,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.importImage(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrawingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Import button
        binding.btnImport.setOnClickListener {
            imagePickerLauncher.launch(arrayOf("image/*"))
        }

        // Undo/Redo
        binding.btnUndo.setOnClickListener {
            binding.drawingView.undo()
            updateUndoRedoButtons()
        }

        binding.btnRedo.setOnClickListener {
            binding.drawingView.redo()
            updateUndoRedoButtons()
        }

        // Save
        binding.btnSave.setOnClickListener {
            if (binding.drawingView.getBackgroundBitmap() != null) {
                viewModel.exportMerged(binding.drawingView)
            } else {
                Toast.makeText(requireContext(), R.string.no_image_to_save, Toast.LENGTH_SHORT).show()
            }
        }

        // Tool selection
        binding.btnPen.setOnClickListener {
            viewModel.setTool(Tool.PEN)
        }

        binding.btnEraser.setOnClickListener {
            viewModel.setTool(Tool.ERASER)
        }

        // Brush size slider
        binding.sliderBrushSize.value = 8f
        binding.sliderBrushSize.addOnChangeListener { _, value, _ ->
            viewModel.setWidth(value)
        }

        // Color palette
        setupColorPalette()

        // Initialize undo/redo button states
        updateUndoRedoButtons()
    }

    private fun setupColorPalette() {
        // Define available colors
        val availableColors = listOf(
            Color.BLACK,
            Color.parseColor("#C62828"),      // Red
            Color.parseColor("#1976D2"),      // Blue
            Color.parseColor("#388E3C"),      // Green
            Color.parseColor("#FFEB3B"),      // Yellow
            Color.parseColor("#F57C00"),      // Orange
            Color.parseColor("#9C27B0"),      // Purple
            Color.parseColor("#795548")       // Brown
        )

        // Initialize adapter
        colorPaletteAdapter = ColorPaletteAdapter { selectedColor ->
            currentSelectedColor = selectedColor
            viewModel.setColor(selectedColor)
            updateColorPalette(availableColors)
        }

        binding.colorPaletteRecycler.adapter = colorPaletteAdapter

        // Set initial palette state
        updateColorPalette(availableColors)
    }

    private fun updateColorPalette(availableColors: List<Int>) {
        val colorItems = availableColors.map { color ->
            ColorItem(
                color = color,
                isSelected = color == currentSelectedColor
            )
        }
        colorPaletteAdapter.submitList(colorItems)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe brush color
                launch {
                    viewModel.brushColor.collect { color ->
                        binding.drawingView.currentColor = color
                    }
                }

                // Observe brush width
                launch {
                    viewModel.brushWidth.collect { width ->
                        binding.drawingView.currentWidthPx = width
                    }
                }

                // Observe tool
                launch {
                    viewModel.tool.collect { tool ->
                        binding.drawingView.currentTool = tool
                        updateToolButtons(tool)
                    }
                }

                // Observe background bitmap
                launch {
                    viewModel.backgroundBitmap.collect { bitmap ->
                        bitmap?.let {
                            binding.drawingView.setBackgroundBitmap(it)
                        }
                    }
                }

                // Observe loading state
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                // Observe export status
                launch {
                    viewModel.exportStatus.collect { status ->
                        when (status) {
                            is DrawingViewModel.ExportStatus.Success -> {
                                Snackbar.make(
                                    binding.root,
                                    R.string.image_saved,
                                    Snackbar.LENGTH_LONG
                                ).show()
                                viewModel.clearExportStatus()
                            }
                            is DrawingViewModel.ExportStatus.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.image_save_failed),
                                    Toast.LENGTH_SHORT
                                ).show()
                                viewModel.clearExportStatus()
                            }
                            else -> {} // Idle or Exporting
                        }
                    }
                }
            }
        }
    }

    private fun updateToolButtons(currentTool: Tool) {
        when (currentTool) {
            Tool.PEN -> {
                binding.btnPen.isEnabled = false
                binding.btnEraser.isEnabled = true
            }
            Tool.ERASER -> {
                binding.btnPen.isEnabled = true
                binding.btnEraser.isEnabled = false
            }
        }
    }

    private fun updateUndoRedoButtons() {
        binding.btnUndo.isEnabled = binding.drawingView.canUndo()
        binding.btnRedo.isEnabled = binding.drawingView.canRedo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
