function(doc){
    if(doc.title != null){
        emit(doc._id, doc.title);
	}
}
