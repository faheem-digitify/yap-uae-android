package co.yap.yapcore.helpers.extentions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import co.yap.networking.customers.requestdtos.Contact
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.managers.SessionManager
import com.google.i18n.phonenumbers.PhoneNumberUtil

fun getLocalContacts(context: Context) = fetchContacts(context)

private fun fetchContacts(context: Context): MutableList<Contact> {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val contacts = LinkedHashMap<Long, Contact>()

        val projection = arrayOf(
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.PHOTO_THUMBNAIL_URI,
            ContactsContract.Data.DATA1,
            ContactsContract.Data.MIMETYPE
        )
        val cursor = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            generateSelection(), null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC"
        )

        if (cursor != null) {
            cursor.moveToFirst()
            val idColumnIndex = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)
            val displayNamePrimaryColumnIndex =
                cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY)
            val thumbnailColumnIndex =
                cursor.getColumnIndex(ContactsContract.Data.PHOTO_THUMBNAIL_URI)
            val mimetypeColumnIndex = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE)
            val dataColumnIndex = cursor.getColumnIndex(ContactsContract.Data.DATA1)
            while (!cursor.isAfterLast) {
                val id = cursor.getLong(idColumnIndex)
                var contact = contacts[id]
                if (contact == null) {

                    contact = Contact()
                    val displayName = cursor.getString(displayNamePrimaryColumnIndex)
                    if (displayName != null && displayName.isNotEmpty()) {
                        contact.title = displayName
                    }
                    mapThumbnail(cursor, contact, thumbnailColumnIndex)
                    contacts[id] = contact
                }
                val mimetype = cursor.getString(mimetypeColumnIndex)
                when (mimetype) {
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE -> mapEmail(
                        cursor,
                        contact,
                        dataColumnIndex
                    )
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {
                        var phoneNumber: String? = cursor.getString(dataColumnIndex)
                        if (phoneNumber != null && phoneNumber.isNotEmpty()) {
                            phoneNumber = phoneNumber.replace("\\s+".toRegex(), "")

                            try {
                                val pn =
                                    PhoneNumberUtil.getInstance().parse(phoneNumber, Utils.getDefaultCountryCode(context))
                                contact.mobileNo = pn.nationalNumber.toString()
                                contact.countryCode = "00${pn.countryCode}"
                            } catch (e: Exception) {
                            }
                        }
                    }
                }
                cursor.moveToNext()
            }
            cursor.close()
        }
        return ArrayList(contacts.values)
    }
    return mutableListOf()
}

private fun generateSelection(): String {
    val mSelectionBuilder = StringBuilder()
    if (mSelectionBuilder.isNotEmpty())
        mSelectionBuilder.append(" AND ")
    mSelectionBuilder.append(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)
        .append(" = 1")
    return mSelectionBuilder.toString()
}

private fun mapThumbnail(cursor: Cursor, contact: Contact, columnIndex: Int) {
    val uri = cursor.getString(columnIndex)
    if (uri != null && uri.isNotEmpty()) {
        contact.beneficiaryPictureUrl = uri
    }
}

private fun mapEmail(cursor: Cursor, contact: Contact, columnIndex: Int) {
    val email = cursor.getString(columnIndex)
    if (email != null && email.isNotEmpty()) {
        contact.email = email
    }
}

fun MutableList<Contact>.removeOwnContact(): MutableList<Contact> {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.removeIf { it.mobileNo == SessionManager.user?.currentCustomer?.mobileNo }
    } else {
        this.remove(this.find { it.mobileNo == SessionManager.user?.currentCustomer?.mobileNo })
    }
    return this
}
