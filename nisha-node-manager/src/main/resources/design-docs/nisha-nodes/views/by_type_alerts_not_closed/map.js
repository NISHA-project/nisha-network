function(doc){
    if(doc.affectedNodeName != null){
        if(doc.alertState != null && doc.alertState != "CLOSED") {
            emit(doc.timestamp, doc._id);
        }
	}
}