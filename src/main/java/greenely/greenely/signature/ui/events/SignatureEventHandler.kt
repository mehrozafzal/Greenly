package greenely.greenely.signature.ui.events

import greenely.greenely.signature.ui.SignatureActivity
import javax.inject.Inject

class SignatureEventHandler @Inject constructor(activity: SignatureActivity) : EventHandler {

    private val eventHandler: EventHandler by lazy {
        val exitHandler = ExitHandler(activity)
        val doneHandler = DoneHandler(activity)
        val readPoaHandler = ReadPoaHandler(activity)
        val readCombinedPoaHandler = ReadPoaHandler(activity)


        exitHandler.next = doneHandler
        doneHandler.next = readPoaHandler
        readPoaHandler.next=readCombinedPoaHandler
        exitHandler
    }

    override fun handleEvent(event: Event) {
        eventHandler.handleEvent(event)
    }
}
