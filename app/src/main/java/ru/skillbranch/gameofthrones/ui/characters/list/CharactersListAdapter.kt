package ru.skillbranch.gameofthrones.ui.characters.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.databinding.ItemCharacterBinding

class CharactersListAdapter : RecyclerView.Adapter<CharactersListAdapter.ViewHolder>() {

    var items: List<CharacterItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .run { ViewHolder(this) }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(private val vb: ItemCharacterBinding) : RecyclerView.ViewHolder(vb.root) {

        fun bind(item: CharacterItem) {
            vb.tvName.text = item.name
            vb.tvTitles.text = item.titles.joinToString(separator = ", ")
        }

    }

}