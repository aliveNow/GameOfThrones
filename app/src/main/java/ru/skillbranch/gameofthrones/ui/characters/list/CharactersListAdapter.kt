package ru.skillbranch.gameofthrones.ui.characters.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.gameofthrones.HouseType
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.databinding.ItemCharacterBinding
import java.lang.NumberFormatException

class CharactersListAdapter(
    private val houseType: HouseType
) : RecyclerView.Adapter<CharactersListAdapter.ViewHolder>() {

    var items: List<CharacterItem> = emptyList()
    var onItemClickListener: ((CharacterItem) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .run { ViewHolder(this) }

    override fun getItemCount(): Int = items.count()

    override fun getItemId(position: Int): Long =
        try {
            items[position].id.toLong()
        } catch (ne: NumberFormatException) {
            super.getItemId(position)
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, houseType.iconId)
        holder.itemView.setOnClickListener { onItemClickListener?.invoke(item) }
    }

    class ViewHolder(private val vb: ItemCharacterBinding) : RecyclerView.ViewHolder(vb.root) {

        fun bind(item: CharacterItem, iconId: Int) {
            with(itemView.context) {
                vb.imgIcon.setImageDrawable(getDrawable(iconId))
                vb.tvName.text = item.name.takeIf { it.isNotEmpty() }
                    ?: getString(R.string.characters_list_information_unknown)
                vb.tvTitles.text =
                    item.titles.joinToString(separator = " • ").takeIf { it.isNotEmpty() }
                        ?: getString(R.string.characters_list_information_unknown)
            }
        }

    }

}