package greenely.greenely.push.models

data class InboxMessage(val body: String,val title:String) : PushMessageVisitable {
    override fun accept(visitor: PushMessageVisitor) {
        visitor.visit(this)
    }
}

