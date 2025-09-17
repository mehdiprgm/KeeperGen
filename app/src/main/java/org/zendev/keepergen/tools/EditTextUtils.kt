package org.zendev.keepergen.tools

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView

fun setEditTextLimiter(editText: EditText, textView: TextView, limit: Int, removeSpaces : Boolean = false) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            var text = editText.text.toString()
            if (removeSpaces) {
                text = text.replace(" ", "");
            }

            if (text.isEmpty()) {
                textView.visibility = View.INVISIBLE
            } else {
                textView.visibility = View.VISIBLE
                textView.text = "${text.length} / $limit"

            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    })
}

fun validateEditTextData(editText: EditText, condition: Boolean, errorMessage: String): Int {
    if (condition) {
        editText.error = errorMessage
        return -1
    }

    editText.error = null
    return 0
}