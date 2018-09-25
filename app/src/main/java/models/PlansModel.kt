package models


data class PlansModel(val response: List<Response>, val code: Int) : BaseModel() {

    data class Response(val id: Int,
                        val plan_id: String,
                        val plan_type: String,
                        val amount: String,
                        val unit: String,
                        val duration: String,
                        val payment_status: String,
                        val is_expired: Int,
                        val purchase_date: String,
                        val remaining_time: String)
}