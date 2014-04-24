function(newDoc, oldDoc, userCtx) {
    if(newDoc._deleted) {
        throw({forbidden : 'you are not allowed to delete documents!'});
    }
    else if(oldDoc != null && oldDoc.role != newDoc.role) {
           throw({forbidden : 'node role is unchangable!'});
   	}
    else if(newDoc.role != null && (newDoc.stateReason == null || newDoc.stateReason == "")) {
   	           throw({forbidden : 'empty state reason not allowed!'});
   	}
   else if(newDoc.role != null && (newDoc.stateReason.length < 10)) {
   	           throw({forbidden : 'state reason too short (at least 10 characters)!'});
   	}
}