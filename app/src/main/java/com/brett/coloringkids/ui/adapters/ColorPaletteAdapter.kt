package com.brett.coloringkids.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.brett.coloringkids.databinding.ItemColorBinding

/**
 * Data class representing a color in the palette
 */
data class ColorItem(
    val color: Int,
    val isSelected: Boolean = false
)

/**
 * RecyclerView adapter for the color palette
 * Uses ListAdapter for efficient updates with DiffUtil
 */
class ColorPaletteAdapter(
    private val onColorSelected: (Int) -> Unit
) : ListAdapter<ColorItem, ColorPaletteAdapter.ColorViewHolder>(ColorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ItemColorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ColorViewHolder(binding, onColorSelected)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ColorViewHolder(
        private val binding: ItemColorBinding,
        private val onColorSelected: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(colorItem: ColorItem) {
            binding.colorSwatch.setBackgroundColor(colorItem.color)

            // Show selection state with stroke
            binding.colorCard.strokeWidth = if (colorItem.isSelected) {
                binding.root.context.resources.displayMetrics.density * 3
            } else {
                0f
            }.toInt()

            binding.colorCard.strokeColor = Color.WHITE

            binding.colorCard.setOnClickListener {
                onColorSelected(colorItem.color)
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates
     */
    private class ColorDiffCallback : DiffUtil.ItemCallback<ColorItem>() {
        override fun areItemsTheSame(oldItem: ColorItem, newItem: ColorItem): Boolean {
            return oldItem.color == newItem.color
        }

        override fun areContentsTheSame(oldItem: ColorItem, newItem: ColorItem): Boolean {
            return oldItem == newItem
        }
    }
}
