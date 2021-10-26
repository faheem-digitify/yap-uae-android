package co.yap.yapcore.enums

enum class EmploymentStatus(val status: String) {
    EMPLOYED("Employed"),
    SELF_EMPLOYED("Self-Employed"),
    SALARIED_AND_SELF_EMPLOYED("Salaried & Self-Employed"),
    OTHER("Other"),
    NONE("None")
}