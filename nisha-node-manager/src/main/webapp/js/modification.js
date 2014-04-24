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

//------    node state reason   ---------------------------------------------------------------------------------

function hideDefaultText () {
    var textVal = document.getElementById("stateReason").value;
    if ( textVal == "[required]"){
        document.getElementById("stateReason").value = "";
    }
}

function restoreDefaultText () {
    var textVal = document.getElementById("stateReason").value;
    if ( textVal == ""){
        document.getElementById("stateReason").value = "[required]";
    }
}

//------    messages multicast control  ---------------------------------------------------------------------------------

//function messageTypeChanged(passToServletBoolean) {
//    var typeSelect = document.getElementById("typeSelect");
//    var selectedText = typeSelect.options[typeSelect.selectedIndex].text;
//    if (selectedText === "MESSAGE") {
//        showMulticast();
//        document.getElementById("filterSupernodesOnly").value = false;
//    } else {
//        showUnicast();
//        document.getElementById("filterSupernodesOnly").value = true;
//    }
//
//    if (passToServletBoolean === true) {
//        document.forms["sendMessageForm"].submit();
//    }
//}

//function showUnicast() {
//    hideMulticast();
//    populateNodeSelect("selectnode1");
//    document.getElementById("rowcast").style.visibility = "hidden";
//
//}

function hideMulticast() {
    var table = document.getElementById("nodeNamesTable");
    for (var i = 3; i < table.rows.length;) {
        //no incrementation becauce length is shrinking; i=3 so that there was header "broadcast row" and "plus row" left
        table.deleteRow(i);
    }
    document.getElementById("plusSpan").style.visibility = "hidden";
}
//
//function showMulticast() {
//    document.getElementById("rowcast").style.visibility = "visible";
//    document.getElementById("plusSpan").style.visibility = "visible";
//    document.getElementById("multicast").checked = "checked";
//    multicastChosen();
//}

function multicastChosen() {
    populateNodeSelect("selectnode1");
    document.getElementById("plusSpan").style.visibility = "visible";
}

function populateNodeSelect(selectId){

    var nodeSelect = document.getElementById(selectId);
    nodeSelect.innerHTML = "";

    var nodesNamesStr = document.getElementById("nodesNamesAsOneString").value;
    var nodesTab = nodesNamesStr.split(" ");
    for(var i = 0; i < nodesTab.length; i++ ) {
        var nodeName = nodesTab[i];
        if (nodeName.trim() !== ""){
            var option = document.createElement('option');
            option.setAttribute('value', nodeName);
            option.innerHTML += nodeName;
            nodeSelect.appendChild(option);
        }
    }

}

function broadcastChosen() {

    hideMulticast();
    var nodeSelect = document.getElementById("selectnode1");
    nodeSelect.innerHTML = "";

    var optionBroadcast = document.createElement('option');
    optionBroadcast.setAttribute('value', 'broadcast');
    optionBroadcast.innerHTML += 'BROADCAST';
    nodeSelect.appendChild(optionBroadcast);
}

//------    messages multicast   ---------------------------------------------------------------------------------

function addNodeRowToTable(plusMinusString) {

    var imgSrc = "img/remove.png";
    if (plusMinusString === "plus") {
        imgSrc = "img/add.png";
    }
    var tabId = "nodeNamesTable";

    var table = document.getElementById(tabId);
    var lastRowId = table.rows[table.rows.length -1].id;
    var index = getIndexFromRowId(lastRowId, 'node');
    var id = "node" + ( parseInt(index) + 1);

    //elements td created as separated nodes so that it works well also in Internet Explorer 9
    var rowElement = document.createElement('tr');
    rowElement.setAttribute('id', 'row' + id);
    var cell1 = document.createElement('td');
    var cell2 = document.createElement('td');
    cell2.className='paddedCell';

    var nodeSelect = document.createElement('select');
    nodeSelect.setAttribute('id', "select" + id);
    nodeSelect.setAttribute('name', "select" + id);
    nodeSelect.className = "messageWideField";

    cell1.appendChild(nodeSelect);

    cell2.innerHTML += '<span class="cursorPointer" onclick="removeNodeRow(this);"><img src="' + imgSrc + '" alt="remove"></span>';

    rowElement.appendChild(cell1);
    rowElement.appendChild(cell2);
    document.getElementById(tabId).firstElementChild.appendChild(rowElement);

    updateParamsNames('nodeNamesTable', 'nodeParamsNames');
    populateNodeSelect(nodeSelect.id);
}

function removeNodeRow(span) {
    var rowToDelete = findElement("TR", span);
    var table = document.getElementById("nodeNamesTable");
    var rowTmp;
    if (rowToDelete != null) {
        for (var i = 0; i < table.rows.length; i++) {
            rowTmp = table.rows[i];
            if (rowTmp == rowToDelete) {
                table.deleteRow(i);
                updateParamsNames('nodeNamesTable', 'nodeParamsNames');     //must be after row delete
                break;
            }
        }
    }
}

function findElement(tagname, startobj) {
    try {
        var resultElement;
        var obj = startobj;
        var tagNameStr = new String(tagname);
        while (obj.parentNode) {
            if (obj.tagName == tagNameStr) {
                resultElement = obj;
                break;
            }
            obj = obj.parentNode;
        }
    } catch(e) {
        alert(e);
    }
    return resultElement;
}

//------  multi attachments     ---------------------------------------------------------------------------------

function addAttachRowToTable(){
    var maxAttachmentsNumber = 5;
    var tabId = "attachmentTable";
    var table = document.getElementById(tabId);

    if (table.rows.length >= maxAttachmentsNumber + 1 ) {   // +1 because header row
        alert("Maximum attachments number: " + maxAttachmentsNumber);
        return;
    }

    var lastRowId = table.rows[table.rows.length - 1].id;
    var id;
    if (lastRowId === "rowattachPlus") {
        id = "attach1";
    }
    else {
        var index = getIndexFromRowId(lastRowId, "attach");
        id = "attach" + ( parseInt(index) + 1);
    }


    //elements td created as separated nodes so that it works well also in Internet Explorer 9
    var rowElement = document.createElement('tr');
    rowElement.setAttribute('id', 'row' + id);
    var cell1 = document.createElement('td');
    var cell2 = document.createElement('td');
    cell2.className='paddedCell';

    cell1.innerHTML += '<input class="messageWideField fileInput" type="file" onkeypress="return noEnterSubmit(event);" id="' + id + '" name="' + id + '">';
    cell2.innerHTML += '<span class="cursorPointer" onclick="removeAttachRow(this);"><img src="img/remove.png" alt="remove"></span>';

    rowElement.appendChild(cell1);
    rowElement.appendChild(cell2);
    document.getElementById(tabId).firstElementChild.appendChild(rowElement);
    updateParamsNames(tabId, "attachParamsNames")
}

function removeAttachRow(elt) {
    var rowToDelete = findElement("TR", elt);
    var table = document.getElementById("attachmentTable");
    var rowTmp;
    if (rowToDelete != null) {
        for (var i = 0; i < table.rows.length; i++) {
            rowTmp = table.rows[i];
            if (rowTmp == rowToDelete) {
                table.deleteRow(i);
                updateParamsNames("attachmentTable", "attachParamsNames");
                break;
            }
        }
    }
}

function updateParamsNames(tabId, targetFieldId) {
    var result = "";
    var table = document.getElementById(tabId);
    for (var i = 0; i < table.rows.length; i++) {
        var rowId = table.rows[i].id;
        if (rowId !== "" && rowId !== "rowcast") {
            if (tabId === "nodeNamesTable") {
                result += rowId.replace("row", "select") + ",";
            } else if (tabId === "attachmentTable") {
                if (rowId !== "rowattachPlus") {
                    result += rowId.replace("row", "") + ",";
                }
            }

        }

    }
    document.getElementById(targetFieldId).value = result;

}

function getIndexFromRowId (rowId, nodeAttachString) {
    if (nodeAttachString === "node") {
        return rowId.replace("rownode", "");
    } else if (nodeAttachString === "attach") {
        return rowId.replace("rowattach", "");
    }
    else return -1;
}

//------  in/outbox checkboxes     ---------------------------------------------------------------------------------
function affectAllCheckboxes() {
    var checkValue = document.getElementById("selectAllCheck").checked;
    var checkboxArray = document.getElementsByClassName("checkboxClass");
    for (var i = 0; i < checkboxArray.length; i++) {
        checkboxArray[i].checked = checkValue;
    }
}

//------  displaying escaped message body (formatted in rows)   ---------------------------------------------------------------------------------
function prepareMessageBody(body, tdId){
    var escapedBR = "&lt;br/&gt;";
    var result = body.split(escapedBR);

    var tdForText = document.getElementById(tdId);
    tdForText.innerHTML = "";
    for(var i = 0; i < result.length; i++) {
        tdForText.innerHTML += result[i] + '<br/>';
    }
}

//resend and draft load
function loadMessageRecipientsToForm() {
    var recipientsAsString = document.getElementById("recipientsAsString").value;
    var strLength = recipientsAsString.length;
    recipientsAsString = recipientsAsString.substring(1, strLength - 1);
    var recipientTab = recipientsAsString.split(", ");
    var recipNumber = recipientTab.length;

    if (recipNumber < 1) {
        return;
    }

    if (recipientTab[0] == 'broadcast') {
        document.getElementById("broadcast").click();
    } else {
        document.getElementById("multicast").click();
        chooseSelectOption("selectnode1", recipientTab[0]);

        var index;
        if (recipNumber > 1) {
            for (var i = 1; i < recipNumber; i++) {
                document.getElementById("plusSpan").click();
                index = i + 1;
                chooseSelectOption("selectnode" + index, recipientTab[i]);
            }
        }
    }
}

function chooseSelectOption(selectId, optionValue) {
    var selectNode = document.getElementById(selectId );
    for(var i = 0; i < selectNode.length; i++) {
        if (selectNode.options[i].value == optionValue) {
            selectNode.options[i].selected = "selected";
            return;
        }
    }
}