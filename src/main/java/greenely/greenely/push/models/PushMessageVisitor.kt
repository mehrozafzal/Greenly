package greenely.greenely.push.models

interface PushMessageVisitor {
    fun visit(dataMessage: DataMessage)
    fun visit(inboxMessage: InboxMessage)
    fun visit(unknownMessage: UnknownMessage)

}

