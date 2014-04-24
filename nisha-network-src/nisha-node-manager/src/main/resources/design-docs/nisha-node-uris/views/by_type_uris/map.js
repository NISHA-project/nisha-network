function(doc){
    if(doc.nodeDomainNameFromRingInfo != null && doc.role != null){
        emit(doc.role, doc);
	}
}
