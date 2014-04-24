function(newDoc, oldDoc, userCtx) {
    if(newDoc._deleted) {
        throw({forbidden : 'you are not allowed to delete documents!'});
    }
    if(oldDoc !== null) {
        //no changes allowed - message state ignored anyway
        if (oldDoc !== newDoc) {
            throw({forbidden : 'you are not allowed to change already sent message!'});
        }
    }
}