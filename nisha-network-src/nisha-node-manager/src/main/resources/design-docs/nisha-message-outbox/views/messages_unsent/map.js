function(doc) {
    if(doc._id !== null && doc.subject !== null) {
        if ((doc.messageState !== null && doc.messageState === "NEW_MESSAGE")) {
            emit(doc._id, doc);
        }

    }
}