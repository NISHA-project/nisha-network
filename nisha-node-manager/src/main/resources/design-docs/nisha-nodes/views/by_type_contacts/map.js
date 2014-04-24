function(doc){
    if(doc.operatorContactId != null){
        emit(doc.operatorContactId, doc);
	}
}
