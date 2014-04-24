function(doc) {
    if(doc.role == "BASICNODE") {
        emit(doc.nodeDomainNameFromRingInfo, null);
    }
}
