package greenely.greenely.push.models

data class UnknownMessage(val body: String, val title:String) : PushMessageVisitable {
    val messageType=MessageType.UNKNOWN
    override fun accept(visitor: PushMessageVisitor) {
        visitor.visit(this)
    }
}