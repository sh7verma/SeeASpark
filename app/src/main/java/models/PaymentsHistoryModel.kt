package models

data class PaymentsHistoryModel(val response: List<PaymentAdditionModel.Response>,
                                val code: Int) : BaseModel()
