package co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.models

import android.os.Parcelable
import androidx.annotation.Keep
import co.yap.networking.coreitems.CoreBottomSheetData
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class EmploymentType(
    var employmentTypeCode: String,
    var employmentType: String
) : Parcelable, CoreBottomSheetData()