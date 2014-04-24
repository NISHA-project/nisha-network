function(newDoc, oldDoc, userCtx) {

    if(newDoc._deleted) {
        throw({forbidden : 'you are not allowed to delete documents!'});
    }
	else if(oldDoc != null && oldDoc.operatorContactId != newDoc.operatorContactId) {
           throw({forbidden : 'it is forbidden to change operator identificator!'});
   	}
    else if(newDoc.operatorContactId != null && newDoc.isBlocked == null) {
        throw({forbidden : 'isBlocked must be defined (true/false)!'});
    }
    else if(newDoc.isBlocked !== null && newDoc.isBlocked === "") {
        throw({forbidden : 'isBlocked cannot be empty! ' + newDoc.isBlocked});
    }
    else if(newDoc.isBlocked != null && !(newDoc.isBlocked == true || newDoc.isBlocked == false)) {
        throw({forbidden : newDoc.isBlocked+' - isBlocked - values allowed are true and false!'});
    }
}
