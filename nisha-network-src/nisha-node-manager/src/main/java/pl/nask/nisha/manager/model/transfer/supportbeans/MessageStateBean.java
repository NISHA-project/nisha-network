package pl.nask.nisha.manager.model.transfer.supportbeans;

import pl.nask.nisha.manager.model.domain.messages.MessageState;


public class MessageStateBean {

    public MessageState[] getMessageStates() {
        return MessageState.values();
    }

    public MessageState getMessageStateNew() {
        return MessageState.NEW_MESSAGE;
    }
}

