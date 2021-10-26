package co.yap.networking.customers.requestdtos

import com.google.gson.annotations.SerializedName

data class EmploymentInfoRequest(
    @SerializedName("employmentStatus")
    val employmentStatus: String? = null,
    @SerializedName("employerName")
    val employerName: String? = null,
    @SerializedName("sponsorName")
    val sponsorName: String? = null,
    @SerializedName("monthlySalary")
    val monthlySalary: String? = null,
    @SerializedName("expectedMonthlyCredit")
    val expectedMonthlyCredit: String? = null,
    @SerializedName("companyName")
    val companyName: String? = null,
    @SerializedName("employmentType")
    val employmentType: String? = null,
    @SerializedName("businessCountries")
    val businessCountries: List<String>? = null,
    @SerializedName("industrySubSegmentCode")
    val industrySegmentCodes: List<String>? = null
)