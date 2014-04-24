/*******************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme
 * European Commission - Directorate-General Home Affairs
 *
 * Contributors:
 *     Research and Academic Computer Network
 ******************************************************************************/

var portMin = 1;
var portMax = 65535;

function loginValidation() {

    var usernm = document.getElementById("operatorId").value;
    var pass = document.getElementById("operatorPassword").value;

    if (!usernm.length > 0) {
        alert("Fill user id");
        return false;
    }
    if (!usernm.trim().length > 0) {
        alert("User id cannot be composited of whitespaces only");
        return false;
    }
    if (usernm.trim().length != usernm.length) {
        alert("User id cannot contain whitespaces");
        return false;
    }
    if (!pass.length > 0) {
        alert("Fill password");
        return false;
    }
    if (!pass.trim().length > 0) {
        alert("Password cannot be composited of whitespaces only");
        return false;
    }
    if (pass.trim().length != pass.length) {
        alert("Password cannot contain whitespaces");
        return false;
    }
    return true;
}

function addNodeFormValidation() {

    if (document.getElementById("nodeDomainNameFromRingInfo").value.length <= 0 ||
        document.getElementById("portNumberFromRingInfo").value.length <= 0 ||
        document.getElementById("role").value.length <= 0 ||
        document.getElementById("stateReason").value.length <= 0
        ) {

        alert("Fill all fields");
        return false;
    }
    else {
        if(!checkPortNumberOK(document.getElementById("portNumberFromRingInfo").value)) {
            alert("port must be a number between " + portMin + " and " + portMax);
            return false;
        }

        var nodeName = document.getElementById("nodeDomainNameFromRingInfo").value;
        if(!stringCharsAsciiPrintable(nodeName)) {
            return false;
        }
    }

    if (document.getElementById("state").value == "REMOVED") {
        return confirm("Remove node with name: " + nodeName + "? Removal is permanent.")
    }

    if (document.getElementById("prevStateReason").value === document.getElementById("stateReason").value) {
        alert("new state requires new state reason");
        return false;
    }

    var minReasonLength = 10;
    if (document.getElementById("stateReason").value == "[required]") {
        alert("Give reason for this state (at least 10 characters)");
        return false;
    } else if (document.getElementById("stateReason").value.length <  minReasonLength) {
        alert(document.getElementById("stateReason").value + " - reason for this state is too short (at least 10 characters)");
        return false;
    }



    return true;
}

function localConfigValidation() {
    var nodeDomainNameFromConfig = document.getElementById("nodeDomainNameFromConfig").value;
    if (!nodeDomainNameFromConfig.length > 0) {
        alert("Fill node name");
        return false;
    }
    if (!nodeDomainNameFromConfig.trim().length > 0) {
        alert("Node name cannot be composited of whitespaces only");
        return false;
    }
    if (nodeDomainNameFromConfig.trim().length != nodeDomainNameFromConfig.length) {
        alert("Node name cannot contain whitespaces");
        return false;
    }
    if (!stringCharsAsciiPrintable(nodeDomainNameFromConfig)) {
        return false;
    }


    if(!checkPortNumberOK(document.getElementById("portNumberFromConfig").value)) {
        alert("port must be a number between " + portMin + " and " + portMax);
        return false;
    }

    if (document.getElementById("firstConfigElt") != null) {

        var opId = document.getElementById("operatorId").value;
        if (!opId.length > 0) {
            alert("Fill operator id");
            return false;
        }
        if (!opId.trim().length > 0) {
            alert("Operator id cannot be composited of whitespaces only");
            return false;
        }
        if (!opId.trim().length > 0) {
            alert("Operator id cannot be composited of whitespaces only");
            return false;
        }

        var opPass = document.getElementById("operatorPassword").value;
        if (!opPass.length > 0) {
            alert("Fill operator password");
            return false;
        }
        if (!opPass.length > 0) {
            alert("Operator password cannot be composited of whitespaces only");
            return false;
        }
        var opEmail = document.getElementById("email").value;
        if(!emailOk(opEmail)) {
            return false;
        }
    }

    return true;
}

function operatorFormValidation(update) {

    var opId = document.getElementById("operatorId").value;
    if (!opId.length > 0) {
        alert("Fill operator id");
        return false;
    }
    if (!opId.trim().length > 0) {
        alert("Operator id cannot be composited of whitespaces only");
        return false;
    }

    var pass;
    if (!update) {
        pass = document.getElementById("operatorPassword").value;
        if (!pass.length > 0) {
            alert("Fill operator password");
            return false;
        }
        if (!pass.trim().length > 0) {
            alert("Operator password cannot be composited of whitespaces only");
            return false;
        }
    }

    var opEmail = document.getElementById("email").value;
    return emailOk(opEmail);
}

function emailOk(opEmail) {
    if (!opEmail.length > 0) {
        alert("Fill operator email");
        return false;
    } else {
        var emailOk = validateEmail(opEmail);

        if (!emailOk) {
            alert("malformed email");
            return false;
        }
    }
    return true;
}

function validateEmail(email) {
    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function stringCharsAsciiPrintable (stringToCheck) {
    for (var i = 0; i < stringToCheck.length; i++) {
        var code = stringToCheck.charCodeAt(i);
        if (code < 33 || code > 126) {
            alert("only ascii printable chars allowed for domain name");
            return false;
        }
    }
    return true;
}

function checkPortNumberOK (port) {
    for(var i = 0; i < port.length; i++) {
        var code = port.charCodeAt(i);
        if (code < 48 || code > 57) {
            return false;
        }
    }
    return !(port < portMin || port > portMax);

}

function sendMessageValidation () {
    return haveSubject() && haveAddresses() && attachmentsSizeOk();
}

function haveAddresses () {
    var nodeParamsNames = document.getElementById('nodeParamsNames').value;
    var paramsNamesTab = nodeParamsNames.split(',');
    var haveRecipients = false;
    for (var i = 0; i < paramsNamesTab.length; i++) {
        var paramName = paramsNamesTab[i];
        if (paramName.length > 0) {
            var address = document.getElementById(paramName).value;
            if (address !== null && address === "choose_recipient") {
                    haveRecipients = false;
                    break;
            } else if (address !== null && address !== "choose recipient") {
                haveRecipients = true;
                break;
            }
        }
    }
    if (!haveRecipients) {
        alert("message must have a recipient");
    }
    return haveRecipients;
}

function haveSubject () {
    if (document.getElementById('subject').value.length > 0) {
        return true;
    } else {
        alert("message must have a subject");
        return false;
    }
}

function attachmentsSizeOk() {
    var fileSize, file;
    var maxAttachmentSize = document.getElementById('maxAttachmentSize').value;
    var maxMB = maxAttachmentSize / 1024 / 1024;

    var attachFieldNameList = document.getElementsByClassName("fileInput");
    for (var i = 0; i < attachFieldNameList.length; i++) {
        file = attachFieldNameList[i].files[0];
        fileSize = file.size;

        if (fileSize > maxAttachmentSize) {
            alert(file.name + " is too big - max size is " + maxMB + "MB (" + maxAttachmentSize + " characters)");
            return false;
        }
    }
    return true;
}

function noEnterSubmit(event) {
    var key = event.keyCode || event.which;
    return key != 13;
}
