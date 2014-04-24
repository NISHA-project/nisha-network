function(doc) {
    if (doc.type === "BroadcastReadLocalInfo") {
        emit(doc._id, doc);
    }
}