function(doc) {
    if(doc.role != null) {
        emit(doc.nodeDomainNameFromRingInfo, null);
    }
}
