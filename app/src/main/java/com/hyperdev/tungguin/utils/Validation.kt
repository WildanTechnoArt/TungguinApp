package com.hyperdev.tungguin.utils

import android.text.TextUtils
import android.util.Patterns

class Validation {

    companion object {
        fun validateFields(name: String): Boolean {

            return !TextUtils.isEmpty(name)
        }

        fun validateEmail(string: String): Boolean {

            return !(TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches())
        }
    }
}