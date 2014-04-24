function(doc){
    if(doc.announcementType != null){
        emit(doc._id, doc);
	}
}
