package ru.skillbranch.gameofthrones.utils.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.ViewCharacterTraitBinding

open class CharacterTraitView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    open var value: String? = null
        set(value) {
            field = value
            findViewById<TextView>(R.id.tvText).text = value
        }

    private val tvTitle: TextView

    init {
        @Suppress("LeakingThis")
        initContent()
        tvTitle = findViewById(R.id.tvTitle)
        val a = context.obtainStyledAttributes(attrs, R.styleable.CharacterTraitView)
        tvTitle.text = a.getString(R.styleable.CharacterTraitView_traitTitle)
        a.recycle()
    }

    protected open fun initContent() {
        ViewCharacterTraitBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setValueOrGone(value: String?) {
        visibility = if (value.isNullOrEmpty()) View.GONE else View.VISIBLE
        this.value = value
    }

}