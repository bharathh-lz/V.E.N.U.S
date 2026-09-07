package com.example.audio

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat

data class VenusContact(
    val name: String,
    val phoneNumber: String,
    val contactType: String = "Mobile"
)

object VenusContactManager {

    /**
     * Searches device contacts matching the requested name or search term.
     */
    fun findContact(context: Context, query: String): VenusContact? {
        val cleanQuery = query.trim().lowercase()
        if (cleanQuery.isBlank()) return null

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE
        )

        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            var bestMatch: VenusContact? = null
            var exactMatch: VenusContact? = null

            cursor?.let {
                val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (it.moveToNext()) {
                    val name = if (nameIdx != -1) it.getString(nameIdx) ?: "" else ""
                    val number = if (numIdx != -1) it.getString(numIdx) ?: "" else ""
                    val nameLower = name.lowercase()

                    if (nameLower == cleanQuery) {
                        exactMatch = VenusContact(name, number)
                        break
                    } else if (nameLower.contains(cleanQuery) || cleanQuery.contains(nameLower)) {
                        if (bestMatch == null) {
                            bestMatch = VenusContact(name, number)
                        }
                    }
                }
            }

            return exactMatch ?: bestMatch
        } catch (_: Exception) {
            return null
        } finally {
            cursor?.close()
        }
    }

    /**
     * Fetches top contacts list for contacts selector in Settings / Control Hub.
     */
    fun getAllContacts(context: Context, limit: Int = 30): List<VenusContact> {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return emptyList()
        }

        val list = mutableListOf<VenusContact>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            cursor?.let {
                val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (it.moveToNext() && list.size < limit) {
                    val name = if (nameIdx != -1) it.getString(nameIdx) ?: "" else ""
                    val number = if (numIdx != -1) it.getString(numIdx) ?: "" else ""
                    if (name.isNotBlank() && number.isNotBlank()) {
                        list.add(VenusContact(name, number))
                    }
                }
            }
        } catch (_: Exception) {
        } finally {
            cursor?.close()
        }

        return list
    }

    /**
     * Initiates a direct phone call or falls back to dialer intent.
     */
    fun makePhoneCall(context: Context, phoneNumber: String): Boolean {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
        if (cleanNumber.isBlank()) return false

        val hasCallPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED

        val intent = if (hasCallPermission) {
            Intent(Intent.ACTION_CALL, Uri.parse("tel:$cleanNumber")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            try {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(dialIntent)
                true
            } catch (_: Exception) {
                false
            }
        }
    }
}
