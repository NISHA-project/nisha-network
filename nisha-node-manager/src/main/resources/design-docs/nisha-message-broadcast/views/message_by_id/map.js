function(doc) {
    if(doc._id !== null && doc.subject !== null) {
        emit(doc._id, doc);
    }
}