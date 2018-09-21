package models


data class PlansModel(val response: List<Response>, val code: Int) : BaseModel() {

    data class Response(val id: Int,
                        val plan_type: String,
                        val cards: String,
                        val time_period: String,
                        val amount: String,
                        val duration: String,
                        val plan_id: String,
                        val is_expired: Int,
                        val remaining_time: String)
}