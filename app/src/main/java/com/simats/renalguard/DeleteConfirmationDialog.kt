package com.simats.renalguard

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button

class DeleteConfirmationDialog(
    context: Context,
    private val onYesClicked: () -> Unit,
    private val onNoClicked: () -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_confirmation)

        val btnYes: Button = findViewById(R.id.btnYes)
        val btnNo: Button = findViewById(R.id.btnNo)

        btnYes.setOnClickListener {
            onYesClicked()
            dismiss()
        }

        btnNo.setOnClickListener {
            onNoClicked()
            dismiss()
        }
    }
}
