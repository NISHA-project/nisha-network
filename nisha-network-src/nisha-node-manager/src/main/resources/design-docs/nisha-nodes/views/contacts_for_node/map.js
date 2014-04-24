function(doc){
    if(doc.operatorContactId != null && doc.contextNodeName != null){
        emit([doc.contextNodeName, doc.operatorContactId], doc);
	}
}
