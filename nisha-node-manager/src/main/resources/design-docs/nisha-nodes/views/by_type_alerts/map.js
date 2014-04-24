function(doc){
    if(doc.affectedNodeName != null){
        emit(doc.timestamp, doc._id);
	}
}
