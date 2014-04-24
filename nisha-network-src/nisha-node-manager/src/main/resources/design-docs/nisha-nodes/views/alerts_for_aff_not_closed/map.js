function(doc){
    if(doc.affectedNodeName != null){
        if(doc.alertState != null && doc.alertState != "CLOSED") {
            emit(doc.affNameTimeViewKey, doc._id);
        }
	}
}