function (newDoc, oldDoc, userCtx) {
    if(newDoc._deleted) {
        throw({forbidden : 'you are not allowed to delete documents!'});
    }

    if(oldDoc !== null) {
        //no changes apart from message state
        if (oldDoc.recipientNodeIds.length != newDoc.recipientNodeIds.length){throw({forbidden : 'you are not allowed to add nor remove message recipientNodeIds!'});}
        else {
            for (var i = 0; i < oldDoc.recipientNodeIds.length; i++) {
                if(oldDoc.recipientNodeIds[i] !== newDoc.recipientNodeIds[i]){throw({forbidden : 'you are not allowed to change message recipientNodeIds!'});}
            }
        }
        if(oldDoc.timeDate !== newDoc.timeDate) {throw({forbidden : 'you are not allowed to change message send time!'});}
        if(oldDoc.subject !== newDoc.subject) {throw({forbidden : 'you are not allowed to change message subject!'});}
        if(oldDoc.body !== newDoc.body) {throw({forbidden : 'you are not allowed to change message body!'});}
        if(oldDoc.senderNodeNamePort !== newDoc.senderNodeNamePort) {throw({forbidden : 'you are not allowed to change message sender node!'});}
        if(oldDoc.senderOperatorFullName !== newDoc.senderOperatorFullName) {throw({forbidden : 'you are not allowed to change message sender operator!'});}
        if(oldDoc.type !== newDoc.type) {throw({forbidden : 'you are not allowed to change message type!'});}
        if(oldDoc.referenceId !== newDoc.referenceId) {throw({forbidden : 'you are not allowed to change message reference!'});}

        if (oldDoc.messageState !== newDoc.messageState) {
            if (newDoc.messageState !== 'ARCHIVED' &&
                newDoc.messageState !== 'DONE' &&
                newDoc.messageState !== 'NEW_MESSAGE' &&
                newDoc.messageState !== 'READ'
                ) {
                throw({forbidden : 'you are not allowed to change message state to: ' + newDoc.messageState});
            }
            else {
                if ((oldDoc.type === 'MESSAGE' || newDoc.type === 'MESSAGE') && newDoc.messageState === 'DONE') {
                    throw({forbidden : 'message state DONE is reserved only for BLOCK and INVALIDATE messages - cannot be applied to messages of type MESSAGE'});
                }
            }
        }


        if (oldDoc._attachments === undefined || oldDoc._attachments === null) {
            if (newDoc._attachments === undefined || newDoc._attachments === null ) {
                //this is ok
            } else {
                throw({forbidden : 'you are not allowed to add message attachments! old: ' + oldDoc._attachments + " new: " + newDoc._attachments});
            }
        }
        else {
            if (newDoc._attachments === undefined || newDoc._attachments === null) {
                throw({forbidden : 'you are not allowed to remove message attachments!'});
            }
            else {
                if (Object.keys(oldDoc._attachments).length !== Object.keys(newDoc._attachments).length) {throw({forbidden : 'you are not allowed to change message attachments!'});}
                else {
                    for (i = 0; i < Object.keys(newDoc._attachments).length; i++) {
                        if(oldDoc._attachments[i] !== newDoc._attachments[i]){throw({forbidden : 'you are not allowed to change message attachments!'});}
                    }
                }
            }
        }


    }
}