function(doc){
    if(doc.operatorId != null){
        emit(doc.operatorId, doc);
	}
}
