package models


data class PaymentAdditionModel(
        val response: Response,
        val code: Int
) : BaseModel() {

    data class Response(
            val id: Int,
            val user_id: String,
            val plan_id: String,
            val plan_type: String,
            val cards: String,
            val unit: String,
            val amount: String,
            val payment_status: String,
            val created_at: String,
            val updated_at: String,
            val purchase_date: String,
            val duration: String,
            val expire_date: String
    )
}