function(doc) {
    if(doc.role == "SUPERNODE" && doc.state == "ACTIVE"){
        emit(doc.nodeDomainNameFromRingInfo, null);
    }
}