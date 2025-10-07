data class PatientProfileResponse(
    val success: Boolean,
    val data: PatientData?
)

data class PatientData(
    val name: String?,
    val email: String?,
    val phone: String?
)

data class BasicResponse(
    val success: Boolean,
    val message: String?
)
