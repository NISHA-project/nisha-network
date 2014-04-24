<%--
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
--%>

<jsp:setProperty name="article" property="*"/>
<%@include file="options.jsp" %>

<c:set var="SUPERNODE" value="SUPERNODE"/>
<c:if test="${not empty loggedOperatorBean.operator.operatorId}">

    <br/><div class="title">Resource's details&nbsp;</div>
    <div id="resourceDetailsPage" class="lower" align="center">
        <table rules="none" class="formTable">
            <c:if test="${thisNodeRoleBean.value eq SUPERNODE and article.status ne 'REMOVED'}">
                <tr>
                    <td></td>
                    <td>
                        <form action="ResourceInvalidator" method="post">
                            <input type="hidden" id="artIdInvalid" name="artId" value="<c:out value="${article._id}"/>"/>
                            <input type="submit" id="invalidateResourceSubmit" name="invalidateResourceSubmit" value="Invalidate">
                        </form>
                    </td>
                </tr>
            </c:if>

            <tr>
                <td style="text-align:right"><label for="title">Title:&nbsp;</label></td>
                <td><input id="title" value="<c:out value="${article.title}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="titleEn">English Title:&nbsp;</label></td>
                <td><input id="titleEn" value="<c:out value="${article.titleEn}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right">Authors:&nbsp;</td>
                <c:set var="authorsCount" value="${fn:length(article.authors)}"/>
                <td>
                    <c:if test="${authorsCount == 0}">
                        <label><input readonly="readonly" size="<c:out value="${field_width}"/>"/></label>
                    </c:if>
                    <c:if test="${authorsCount gt 1}">
                        <hr/>
                    </c:if>
                    <c:forEach var="author" items="${article.authors}">
                        <label><input value="<c:out value="${author}"/>"
                                      readonly="readonly" size="<c:out value="${field_width}"/>"/></label><br/>
                    </c:forEach>
                    <c:if test="${authorsCount gt 1}">
                        <hr/>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="type">Type:&nbsp;</label></td>
                <td><input id="type" value="<c:out value="${article.type}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="status">Status:&nbsp;</label></td>
                <td><input id="status" value="<c:out value="${article.status}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="ownerNodeGUID">Owner node:&nbsp;</label></td>
                <td><input id="ownerNodeGUID" value="<c:out value="${article.ownerNodeGUID}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="description">Description:&nbsp;</label></td>
                <td><textarea class="textAreaInForm" id="description" name="description" cols="" rows="4"
                              readonly="readonly"><c:out value="${article.description}"/></textarea><br/>
                </td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="descriptionEn">English Description:&nbsp;</label></td>
                <td><textarea class="textAreaInForm" id="descriptionEn" name="descriptionEn" cols="" rows="4"
                              readonly="readonly"><c:out value="${article.descriptionEn}"/></textarea><br/>
                </td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="creationDate">Creation date:&nbsp;</label></td>
                <td><input id="creationDate" value="<c:out value="${article.creationDate}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="lastChangeDate">Last change date:&nbsp;</label></td>
                <td><input id="lastChangeDate" value="<c:out value="${article.lastChangeDate}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <%--------------------------------------------------------------------------------------------------%>
            <c:set var="extCounter" value="${fn:length(article.externalInfo)}"/>
            <c:set var="first" value="true"/>
            <c:forEach var="ext" items="${article.externalInfo}">
                <tr>
                    <td style="text-align:right"><label for="externalInfoUrl">External info:&nbsp;</label></td>
                    <td>
                        <c:if test="${extCounter gt 0 and first eq 'true'}">
                            <hr/>
                            <c:set var="first" value="false"/>
                        </c:if>
                        <input id="externalInfoUrl" value="<c:out value="URL: ${ext.URL}"/>" readonly="readonly"
                               size="<c:out value="${field_width}"/>"/><br/>
                        <label><input id="externalInfoName" value="<c:out value="Name: ${ext.name}"/>"
                                      readonly="readonly"
                                      size="<c:out value="${field_width}"/>"/></label><br/>
                        <label><input id="externalInfoAccessDate"
                                      value="<c:out value="Access date: ${ext.accessDate}"/>"
                                      readonly="readonly" size="<c:out value="${field_width}"/>"/></label><br/>
                        <label><input id="externalInfoOwner" value="<c:out value="Owner: ${ext.owner}"/>"
                                      readonly="readonly"
                                      size="<c:out value="${field_width}"/>"/></label><br/>
                        <c:if test="${extCounter gt 0}">
                            <hr/>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <%--------------------------------------------------------------------------------------------------%>
            <tr>
                <td style="text-align:right"><label for="language">Language:&nbsp;</label></td>
                <td><input id="language" value="<c:out value="${article.language}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="keywords">Keywords:&nbsp;</label></td>
                <td><input id="keywords" value="<c:out value="${article.keywords}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="tags">Tags:&nbsp;</label></td>
                <td><input id="tags" value="<c:out value="${article.tags}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="targetGroups">Target groups:&nbsp;</label></td>
                <td><input id="targetGroups" value="<c:out value="${article.targetGroups}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <%--------------------------------------------------------------------------------------------------%>
            <c:set var="relatedCounter" value="${fn:length(article.related)}"/>
            <c:set var="first" value="true"/>
            <c:forEach var="rel" items="${article.related}">
                <tr>
                    <td style="text-align:right"><label for="relatedUUID">Related:&nbsp;</label></td>
                    <td>
                        <c:if test="${relatedCounter gt 0 and first eq 'true'}">
                            <hr/>
                            <c:set var="first" value="false"/>
                        </c:if>
                        <input id="relatedUUID" value="<c:out value="id: ${rel.docGUID}"/>" readonly="readonly"
                               size="<c:out value="${field_width}"/>"/><br/>
                        <label><input id="relatedRev" value="<c:out value="rev: ${rel.docREV}"/>" readonly="readonly"
                                      size="<c:out value="${field_width}"/>"/></label><br/>
                        <hr/>
                    </td>
                </tr>
            </c:forEach>
            <%--------------------------------------------------------------------------------------------------%>
            <tr>
                <td style="text-align:right"><label for="operatorRating">Operator rating:&nbsp;</label></td>
                <td><input id="operatorRating" value="<c:out value="${article.operatorRating}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="userRating">User rating:&nbsp;</label></td>
                <td><input id="userRating" value="<c:out value="${article.userRating}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="supervisor">Supervisor:&nbsp;</label></td>
                <td><input id="supervisor" value="<c:out value="${article.supervisor}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="translationBase">Translation base:&nbsp;</label></td>
                <td><input id="translationBase" value="<c:out value="${article.translationBase}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="portalId">Portal Id:&nbsp;</label></td>
                <td><input id="portalId" value="<c:out value="${article.portalId}"/>"
                           readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
            </tr>
            <%--------------------------------------------------------------------------------------------------%>
            <c:set var="resourceCounter" value="${fn:length(article.resources)}"/>
            <c:set var="first" value="true"/>
            <c:forEach var="res" items="${article.resources}">
                <tr>
                    <td style="text-align:right"><label for="resourcesUrl">Resources:&nbsp;</label></td>
                    <td>
                        <c:if test="${resourceCounter gt 0 and first eq 'true'}">
                            <hr/>
                            <c:set var="first" value="false"/>
                        </c:if>
                        <input id="resourcesUrl" value="<c:out value="Res. URL: ${res.URL}"/>" readonly="readonly"
                               size="<c:out value="${field_width}"/>"/><br/>
                        <label><input id="resourcesName" value="<c:out value="Res. Name: ${res.name}"/>" readonly="readonly"
                                      size="<c:out value="${field_width}"/>"/></label><br/>
                        <label><input id="resourcesAccessDate" value="<c:out value="Res. Access date: ${res.accessDate}"/>" readonly="readonly"
                                                              size="<c:out value="${field_width}"/>"/></label><br/>
                        <c:if test="${resourceCounter gt 0}">
                            <hr/>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <%--------------------------------------------------------------------------------------------------%>
            <c:if test="${article.specialInfo ne null}">
                <c:set var="special" value="${article.specialInfo}"/>
                <tr>
                    <td class="boldText rightText">Special info&nbsp;</td>
                    <td></td>
                </tr>
                <tr><td class="rightText"><label for="damageDescription">Damage description:</label></td>
                    <td>
                        <input id="damageDescription" value="<c:out value="${special.damageDescription}"/>"
                                      readonly="readonly" size="<c:out value="${field_width}"/>"/><br/>
                    </td>
                </tr>
                <tr><td class="rightText"><label for="vulnerabilityDescription">Vulnerability description:</label></td>
                    <td>
                        <input id="vulnerabilityDescription" value="<c:out value="${special.vulnerabilityDescription}"/>"
                            readonly="readonly" size="<c:out value="${field_width}"/>"/><br/>
                    </td>
                </tr>
                <tr><td class="rightText"><label for="consequencesDescription">Consequences description:</label></td>
                    <td>
                        <input id="consequencesDescription" value="<c:out value="${special.consequencesDescription}"/>"
                            readonly="readonly" size="<c:out value="${field_width}"/>"/><br/>
                    </td>
                </tr>
                <tr><td class="rightText"><label for="possibleSolution">Possible solution:</label></td>
                    <td>
                        <input id="possibleSolution" value="<c:out value="${special.possibleSolution}"/>"
                            readonly="readonly" size="<c:out value="${field_width}"/>"/><br/>
                    </td>
                </tr>
                <tr><td class="rightText"><label for="CVEID">CVE Id:</label></td>
                    <td>
                        <input id="CVEID" value="<c:out value="${special.CVEID}"/>"
                            readonly="readonly" size="<c:out value="${field_width}"/>"/><br/>
                    </td>
                </tr>
                <tr><td class="rightText"><label for="CVEDate">CVE Date:</label></td>
                    <td>
                        <input id="CVEDate" value="<c:out value="${special.CVEDate}"/>"
                            readonly="readonly" size="<c:out value="${field_width}"/>"/><br/>
                    </td>
                </tr>
                <tr><td class="rightText"><label for="CVEAuthor">CVE Author:</label></td>
                    <td>
                        <input id="CVEAuthor" value="<c:out value="${special.CVEAuthor}"/>"
                        readonly="readonly" size="<c:out value="${field_width}"/>"/><br/>
                    </td>
                </tr>
                <tr><td class="rightText"><label for="propagationMethod">Propagation method:</label></td>
                    <td>
                        <input id="propagationMethod" value="<c:out value="${special.propagationMethod}"/>"
                            readonly="readonly" size="<c:out value="${field_width}"/>"/><br/>
                    </td>
                </tr>
                <tr><td class="rightText"><label for="exploitationProbability">Exploitation probability:</label></td>
                    <td>
                        <input id="exploitationProbability" value="<c:out value="${special.exploitationProbability}"/>"
                            readonly="readonly" size="<c:out value="${field_width}"/>"/><br/>
                    </td>
                </tr>
                <tr><td class="rightText"><label for="exploitationDamageLevel">Exploitation damage level:</label></td>
                    <td>
                        <input id="exploitationDamageLevel" value="<c:out value="${special.exploitationDamageLevel}"/>"
                            readonly="readonly" size="<c:out value="${field_width}"/>"/><br/>
                    </td>
                </tr>
                <c:forEach var="soft" items="${special.vulnerableSoftware}">
                    <tr><td class="rightText"><label for="vulnerableSoftware">Vulnerable software:</label></td>
                        <td>
                            <hr class="dashedLine">
                            <input id="vulnerableSoftware" value="<c:out value="${soft.softwareName} "/>"
                                readonly="readonly" size="<c:out value="${field_width}"/>"/><br/>
                            <c:forEach var="version" items="${soft.softwareVersion}">
                                <label><textarea class="textAreaInForm" id="softVersion" name="softVersion" cols="" rows="2"
                                          readonly="readonly"><c:out value="version: ${version.version}
platform: ${version.platform}"/></textarea></label> <%--intended line break here--%>
                            </c:forEach>
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
            <%--------------------------------------------------------------------------------------------------%>
        </table>
    </div>
</c:if>

<%@include file="foot.jsp" %>