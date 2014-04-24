function(doc){
    if(doc.detectingNodeName != null){
        emit(doc.detNameTimeViewKey, doc._id);
	}
}
