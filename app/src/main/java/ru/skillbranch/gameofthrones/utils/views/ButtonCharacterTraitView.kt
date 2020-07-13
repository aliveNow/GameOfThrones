package ru.skillbranch.gameofthrones.utils.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import ru.skillbranch.gameofthrones.databinding.ViewButtonCharacterTraitBinding

class ButtonCharacterTraitView(context: Context, attrs: AttributeSet) :
    CharacterTraitView(context, attrs) {

    var onButtonClicked: ((tag: String?) -> Unit)? = null

    override fun initContent() {
        val vb = ViewButtonCharacterTraitBinding.inflate(LayoutInflater.from(context), this)
        vb.tvValue.setOnClickListener {
            onButtonClicked?.invoke(tag?.toString())
        }
    }

}