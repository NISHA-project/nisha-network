function(doc) {
    if(doc.title != null){
        emit(doc.title, doc._id);
    }
}