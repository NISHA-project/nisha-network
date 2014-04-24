function(doc) {
    if(doc.role == "SUPERNODE") {
        emit(doc.nodeDomainNameFromRingInfo, null);
    }
}