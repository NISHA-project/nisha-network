function(doc) {
    if(doc._id !== null && doc.timeDate !== null &&
        (doc.messageState !== null && doc.messageState !== "ARCHIVED")) {
        emit(doc.timeDate, doc._id);
    }
}