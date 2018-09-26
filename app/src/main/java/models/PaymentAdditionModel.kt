package models


data class PaymentAdditionModel(
        val response: PlansModel.Response,
        val code: Int) : BaseModel()