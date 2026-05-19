package com.healthcare.raktavahini.utils

object Constants {
    val BLOOD_GROUPS = arrayOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    val URGENCY_LEVELS = arrayOf("Critical", "Urgent", "Today", "Planned")
    const val ELIGIBILITY_DAYS = 90
    const val MIN_AGE = 18
    const val MAX_AGE = 65

    val BLOOD_COMPATIBILITY = mapOf(
        "A+" to "Can receive from A+, A-, O+, O-",
        "A-" to "Can receive from A-, O-",
        "B+" to "Can receive from B+, B-, O+, O-",
        "B-" to "Can receive from B-, O-",
        "AB+" to "Universal recipient: can receive from all groups",
        "AB-" to "Can receive from AB-, A-, B-, O-",
        "O+" to "Can receive from O+, O-",
        "O-" to "Universal donor group, can receive only from O-"
    )
}
