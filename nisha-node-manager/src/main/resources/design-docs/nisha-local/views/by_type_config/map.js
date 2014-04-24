function(doc){
    if(doc.nodeDomainNameFromConfig != null){
        emit(doc.nodeDomainNameFromConfig, doc);
	}
}
