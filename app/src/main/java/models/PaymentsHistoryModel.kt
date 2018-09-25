package models

data class PaymentsHistoryModel(val response: List<PlansModel.Response>,
                                val code: Int) : BaseModel()
