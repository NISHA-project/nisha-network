function(doc){
    if(doc.detectingNodeName != null){
        if(doc.alertState != null && doc.alertState != "CLOSED") {
            emit(doc.detNameTimeViewKey, doc._id);
        }
	}
}