function(newDoc, oldDoc, userCtx) {
    if(newDoc._deleted) {
        throw({forbidden : 'you are not allowed to delete documents!'});
    }
}