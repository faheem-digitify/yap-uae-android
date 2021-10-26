package co.yap.modules.location.kyc_additional_info.employment_info.status

import co.yap.yapcore.enums.EmploymentStatus

data class EmploymentStatusSelectionModel(
    val employmentStatus: EmploymentStatus,
    val infoStatus: String,
    var isSelected: Boolean
)
