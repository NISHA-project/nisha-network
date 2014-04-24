function(doc){
    if(doc.affectedNodeName != null){
        emit(doc.affNameTimeViewKey, doc._id);
	}
}
