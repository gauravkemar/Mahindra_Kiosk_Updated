package com.kemarport.mahindrakiosk.helper

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.EditText

import android.widget.PopupWindow
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.unloadingconfirmation.UnloadingConfirmationActivity

class CustomKeyboard(private val context: Context, private val keyboardView: View) {

    private val popupWindow: PopupWindow = PopupWindow(keyboardView)
    private var currentEditText: EditText? = null
    private var activityContext:UnloadingConfirmationActivity? = null

    init {
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT

        setupKeyboardButtons()
    }

    private fun setupKeyboardButtons() {
        val cardViewIds = arrayOf(
            R.id.mcv_num_1, R.id.mcv_num_2, R.id.mcv_num_3,
            R.id.mcv_num_4, R.id.mcv_num_5, R.id.mcv_num_6,
            R.id.mcv_num_7, R.id.mcv_num_8, R.id.mcv_num_9,
            R.id.mcv_num_0, R.id.mcv_clear
        )

        for (cardViewId in cardViewIds) {
            val cardView = keyboardView.findViewById<MaterialCardView>(cardViewId)
            cardView.setOnClickListener { onKeyboardButtonClicked(cardView) }
        }
    }
    private fun onKeyboardButtonClicked(cardView: MaterialCardView) {
        val editText = currentEditText
        val buttonText: String
        val textView0 = cardView.findViewById<TextView>(R.id.tv_1)
        val textView1 = cardView.findViewById<TextView>(R.id.tv_2)
        val textView2 = cardView.findViewById<TextView>(R.id.tv_3)
        val textView3 = cardView.findViewById<TextView>(R.id.tv_4)
        val textView4 = cardView.findViewById<TextView>(R.id.tv_5)
        val textView5 = cardView.findViewById<TextView>(R.id.tv_6)
        val textView6 = cardView.findViewById<TextView>(R.id.tv_7)
        val textView7 = cardView.findViewById<TextView>(R.id.tv_8)
        val textView8 = cardView.findViewById<TextView>(R.id.tv_9)
        val textView9 = cardView.findViewById<TextView>(R.id.tv_0)
        val tvClear = cardView.findViewById<TextView>(R.id.tv_clear)
        if (textView0 != null) {
            buttonText = textView0.text.toString()
        } else if(textView1 != null) {
            buttonText = textView1.text.toString()
        } else if(textView2 != null) {
            buttonText = textView2.text.toString()
        } else if(textView3 != null) {
            buttonText = textView3.text.toString()
        } else if(textView4 != null) {
            buttonText = textView4.text.toString()
        } else if(textView5 != null) {
            buttonText = textView5.text.toString()
        } else if(textView6 != null) {
            buttonText = textView6.text.toString()
        }else if(textView7 != null) {
            buttonText = textView7.text.toString()
        }else if(textView8 != null) {
            buttonText = textView8.text.toString()
        }else if(textView9 != null) {
            buttonText = textView9.text.toString()
        }else if(tvClear != null) {
            buttonText = tvClear.text.toString()
        }
        else{
            buttonText=""
        }

        if (buttonText == "clear") {
            editText?.text?.clear()
            activityContext?.refreshIcons()
        } else {
            val selectionStart = editText?.selectionStart
            val selectionEnd = editText?.selectionEnd
            selectionStart?.let { selectionEnd?.let { it1 -> Math.min(it, it1) } }?.let {
                selectionEnd?.let { it1 -> Math.max(selectionStart, it1) }?.let { it2 ->
                    editText?.text?.replace(
                        it,
                        it2,
                        buttonText
                    )
                }
            }
        }
    }

    fun showAt(
        anchor: EditText,
        offsetHeight: Int,
        unloadingConfirmationActivity: UnloadingConfirmationActivity
    ) {
        this.activityContext=unloadingConfirmationActivity
        this.currentEditText=anchor
        val xOffset = anchor.width+10 // No horizontal offset
        val yOffset = -anchor.height-offsetHeight // No vertical offset
        popupWindow.showAsDropDown(anchor, xOffset, yOffset)
    }
/*    fun showAt(anchor: EditText,offsetHeight:Int) {
        this.currentEditText = anchor

        // Check if the soft keyboard is currently visible
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val isKeyboardVisible = inputMethodManager.isActive(anchor)

        // Hide the soft keyboard if it's visible
        if (isKeyboardVisible) {
            inputMethodManager.hideSoftInputFromWindow(anchor.windowToken, 0)
        }

        val xOffset = anchor.width // No horizontal offset
        val yOffset = -anchor.height-offsetHeight // No vertical offset

        // Create a new PopupWindow and set its properties
        val popupWindow = PopupWindow(keyboardView)
        popupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.isOutsideTouchable = false // Prevent dismissing by tapping outside
        popupWindow.isFocusable = true // Allow the popup to receive focus events

        // Show the popup window as a dropdown view below the anchor
        popupWindow.showAsDropDown(anchor, xOffset, yOffset)
    }*/

/*    private fun calculateYOffset(anchor: EditText): Int {
        val screenHeight = context.resources.displayMetrics.heightPixels
        val customKeyboardHeight = keyboardView.height

        // Calculate the distance from the bottom of the screen to the top of the keyboard layout
        val keyboardTop = screenHeight - customKeyboardHeight

        // Calculate the current EditText's position on the screen
        val editTextLocation = IntArray(2)
        anchor.getLocationOnScreen(editTextLocation)

        // Calculate the y-offset to ensure the keyboard layout is within the screen bounds
        return keyboardTop - editTextLocation[1]
    }*/

    fun dismiss() {
        popupWindow.dismiss()
    }
}