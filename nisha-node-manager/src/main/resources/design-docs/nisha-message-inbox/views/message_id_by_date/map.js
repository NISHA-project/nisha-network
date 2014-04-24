function(doc) {
    if(doc._id !== null && doc.timeDate !== null) {
        emit(doc.timeDate, doc._id);
    }
}