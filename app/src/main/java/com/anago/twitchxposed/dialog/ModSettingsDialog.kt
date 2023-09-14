package com.anago.twitchxposed.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.anago.twitchxposed.MainHook.Companion.modResource
import com.anago.twitchxposed.R
import com.anago.twitchxposed.pref.PRefs.enableAutoClaimPoints
import com.anago.twitchxposed.pref.PRefs.enablePreventMessages
import com.anago.twitchxposed.utils.ViewUtils.layoutInflater

class ModSettingsDialog(private val context: Context) {
    fun show() {
        val settingsLayoutId = modResource.getLayout(R.layout.dialog_mod_settings)
        val settingsLayout = context.layoutInflater.inflate(
            settingsLayoutId,
            null,
            false
        ) as ViewGroup
        settingsLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        AlertDialog.Builder(context, R.style.DialogTheme).apply {
            setView(settingsLayout)
        }.show()

        settingsLayout.addView(createItem(
            "Auto Claim Points",
            R.drawable.ic_currency_exchange,
            "claim bonus points automatically",
            useSwitch = true,
            isCheckedd = enableAutoClaimPoints,
            onClick = null,
            onCheck = { isChecked ->
                enableAutoClaimPoints = isChecked
            }
        ))

        settingsLayout.addView(createItem(
            "Prevent Messages",
            R.drawable.ic_contract_delete,
            "prevent messages from clear by moderator",
            useSwitch = true,
            isCheckedd = enablePreventMessages,
            onClick = null,
            onCheck = { isChecked ->
                enablePreventMessages = isChecked
            }
        ))
    }

    private fun createItem(
        title: String,
        iconResId: Int? = 0,
        description: String? = null,
        useSwitch: Boolean = false,
        isCheckedd: Boolean = false,
        onCheck: ((isChecked: Boolean) -> Unit)?,
        onClick: (() -> Unit)?
    ): View {
        val itemLayoutId = modResource.getLayout(R.layout.settings_item)
        val settingsLayout = context.layoutInflater.inflate(itemLayoutId, null, false)

        settingsLayout.findViewById<TextView>(R.id.title).apply {
            text = title
        }

        settingsLayout.findViewById<ImageView>(R.id.icon).apply {
            if (iconResId == null || iconResId == 0) {
                visibility = View.GONE
            } else {
                setImageDrawable(ResourcesCompat.getDrawable(modResource, iconResId, null))
            }
        }

        settingsLayout.findViewById<TextView>(R.id.description).apply {
            text = description
        }

        settingsLayout.findViewById<Switch>(R.id.switchh).apply {
            if (useSwitch) {
                isChecked = isCheckedd
                visibility = View.VISIBLE
                setOnCheckedChangeListener { _, isChecked ->
                    onCheck?.invoke(isChecked)
                }
            }
        }

        if (onClick != null) {
            settingsLayout.setOnClickListener {
                onClick.invoke()
            }
        }

        return settingsLayout
    }
}