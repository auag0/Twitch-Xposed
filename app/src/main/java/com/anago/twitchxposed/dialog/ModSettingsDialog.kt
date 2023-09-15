package com.anago.twitchxposed.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.anago.twitchxposed.MainHook.Companion.modResource
import com.anago.twitchxposed.R
import com.anago.twitchxposed.database.AppDatabase.Companion.getDB
import com.anago.twitchxposed.pref.PRefs.enableAutoClaimPoints
import com.anago.twitchxposed.pref.PRefs.enableMessageLogs
import com.anago.twitchxposed.pref.PRefs.enablePreventMessages
import com.anago.twitchxposed.utils.ViewUtils.layoutInflater
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModSettingsDialog(private val context: Context) {
    @OptIn(DelicateCoroutinesApi::class)
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

        settingsLayout.addView(
            createItem(
                "Settings",
                0,
                null,
                useSwitch = false,
                onClick = null,
                onCheck = null,
                isHeader = true
            )
        )

        settingsLayout.addView(createItem(
            "Auto Claim Points",
            R.drawable.ic_currency_exchange,
            "claim bonus points automatically",
            useSwitch = true,
            isCheckedd = enableAutoClaimPoints,
            onClick = null,
            onCheck = { isChecked ->
                enableAutoClaimPoints = isChecked
            },
            isHeader = false
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
            },
            isHeader = false
        ))

        settingsLayout.addView(
            createItem(
                "Message Logs",
                R.drawable.ic_chat,
                "Enable save messages",
                useSwitch = true,
                isCheckedd = enableMessageLogs,
                onClick = null,
                onCheck = { isChecked ->
                    enableMessageLogs = isChecked
                },
                isHeader = false
            )
        )

        settingsLayout.addView(
            createItem(
                "Delete all Saved message Logs",
                R.drawable.ic_delete,
                "loading..",
                useSwitch = false,
                isCheckedd = false,
                onClick = {
                    AlertDialog.Builder(context).apply {
                        setTitle("Confirm")
                        setPositiveButton("delete") { _, _ ->
                            GlobalScope.launch(Dispatchers.IO) {
                                getDB().userChatMessageDao().deleteAllMessageLogs()
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                    findViewById<TextView>(R.id.description).apply {
                                        text = "0 logs"
                                    }
                                }
                            }
                        }
                    }.show()
                },
                onCheck = null,
                isHeader = false,
                func = {
                    GlobalScope.launch(Dispatchers.IO) {
                        val count = getDB().userChatMessageDao().getAllMessageCount()
                        withContext(Dispatchers.Main) {
                            findViewById<TextView>(R.id.description).apply {
                                text = "$count logs"
                            }
                        }
                    }
                }
            )
        )

        settingsLayout.addView(
            createItem(
                "Account",
                0,
                null,
                useSwitch = false,
                onClick = null,
                onCheck = null,
                isHeader = true
            )
        )

        val sp = context.getSharedPreferences("user", MODE_PRIVATE)

        settingsLayout.addView(
            createItem(
                "Your AuthToken",
                R.drawable.ic_key,
                "click to show",
                useSwitch = false,
                onClick = {
                    val authToken: String? = sp.getString("authToken_v2", null)
                    findViewById<TextView>(R.id.description).apply {
                        if (authToken.isNullOrBlank()) {
                            text = "not found."
                            setTextIsSelectable(false)
                        } else {
                            text = authToken
                            setTextIsSelectable(true)
                        }
                    }
                },
                onCheck = null,
                isHeader = false
            )
        )
    }

    private fun createItem(
        title: String,
        iconResId: Int? = 0,
        description: String? = null,
        useSwitch: Boolean = false,
        isCheckedd: Boolean = false,
        onCheck: ((isChecked: Boolean) -> Unit)? = null,
        onClick: (View.() -> Unit)? = null,
        isHeader: Boolean = false,
        func: (View.() -> Unit)? = null
    ): View {
        val itemLayoutId = modResource.getLayout(R.layout.settings_item)
        val settingsLayout = context.layoutInflater.inflate(itemLayoutId, null, false)

        settingsLayout.findViewById<TextView>(R.id.title).apply {
            text = title
            if (isHeader) {
                gravity = Gravity.CENTER
                textSize = 18f
            }
        }

        settingsLayout.findViewById<ImageView>(R.id.icon).apply {
            if (iconResId == null || iconResId == 0) {
                visibility = View.GONE
            } else {
                setImageDrawable(ResourcesCompat.getDrawable(modResource, iconResId, null))
            }
        }

        settingsLayout.findViewById<TextView>(R.id.description).apply {
            if (description == null) {
                visibility = View.GONE
            } else {
                text = description
            }
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
                onClick.invoke(it)
            }
        }

        func?.invoke(settingsLayout)

        return settingsLayout
    }
}