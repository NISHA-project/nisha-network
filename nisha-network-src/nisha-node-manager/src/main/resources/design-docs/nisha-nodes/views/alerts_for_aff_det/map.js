function(doc) {
    if (doc.affectedNodeName != null && doc.detectingNodeName && doc.alertState) {
        emit([doc.affectedNodeName, doc.detectingNodeName, doc.alertState], doc._id);
    }
}