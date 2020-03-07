package greenely.greenely.push.models

data class DataMessage(val body: String, val title: String, val deepLink: MessageType) : PushMessageVisitable {
    override fun accept(visitor: PushMessageVisitor) {
        visitor.visit(this)
    }
}


