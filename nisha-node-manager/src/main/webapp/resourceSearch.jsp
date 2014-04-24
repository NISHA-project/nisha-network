<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="resourceSearchPage" class="lower" align="center">

    <form action="ResourcePagination" method="post">
        <table style="width: 60%;" rules="none">
            <tr>
                <td style="text-align: right"><label for="resourceQuery">Find resource<br>starting with:&nbsp;</label></td>
                <td colspan="2">
                    <input name="resourceQuery" id="resourceQuery" type="text" style="width: 100%;"
                           value="<c:out value="${param.resourceQuery}"/>" />
                </td>
                <td><input name="resourceSearchSubmit" type="submit" value="Search"></td>
            </tr>
            <tr>
                <td style="text-align: right"></td>
                <td>
                    <label>Query context:
                        <input type="radio" name="queryType" value="title"
                               <c:if test="${param.queryType == 'title'}"> checked="checked" </c:if> >title
                    </label>
                    <label>
                        <input type="radio" name="queryType" value="id"
                        <c:if test="${param.queryType != 'title'}"> checked="checked" </c:if> >id
                    </label>
                </td>
                <td align="right">
                    <label>Range:
                        <input type="radio" name="searchRange" value="global"
                                <c:if test="${param.searchRange == 'global'}"> checked="checked" </c:if> >global
                    </label>
                    <c:if test="${param.isBasicnode eq 'true'}">
                        <label>
                            <input type="radio" name="searchRange" value="local"
                                <c:if test="${param.searchRange != 'global'}"> checked="checked" </c:if> >local
                        </label>
                    </c:if>
                </td>
                <td></td>
            </tr>
        </table>
    </form>
    <hr/>
</div>