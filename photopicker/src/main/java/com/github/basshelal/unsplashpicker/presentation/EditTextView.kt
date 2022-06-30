package com.github.basshelal.unsplashpicker.presentation

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

internal class EditTextView
@JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyle: Int = 0
) : AppCompatEditText(context, attributeSet, defStyle) {

    internal var isEditable: Boolean = true
        set(value) {
            field = value
            isCursorVisible = value
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                showSoftInputOnFocus = value
            }
            isFocusableInTouchMode = value
            isFocusable = value
            isClickable = value
        }

    init {
        isEditable = true
    }

}