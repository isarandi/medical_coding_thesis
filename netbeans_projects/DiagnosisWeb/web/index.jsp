<%@page import="diag.Result"%>
<%@page import="java.util.List"%>
<%@page import="diag.Communicator"%>
<%@page contentType="application/xhtml+xml" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link rel="stylesheet" type="text/css" href="style.css" />
        <title>Diagnóziskódoló</title>
    </head>
    <body>
        <h1>Diagnóziskódoló</h1>

        <%
            request.setCharacterEncoding("UTF-8");
            String diagnosis = request.getParameter("diagnosis");
        %>
        <form action="index.jsp" method="get">
            <div class="searching">
                <h2>Kódolás</h2>
                <span>Diagnózis:</span>
                <jsp:element name="input">
                    <jsp:attribute name="type">text</jsp:attribute>
                    <jsp:attribute name="size">100</jsp:attribute>
                    <jsp:attribute name="value"><%=(diagnosis!=null)?diagnosis:""%></jsp:attribute>
                    <jsp:attribute name="name">diagnosis</jsp:attribute>
                    <jsp:attribute name="id">diagnosis</jsp:attribute>
                </jsp:element>
                <input type="submit" value="Küldés" />
            </div>
        </form>

        <%
            if (diagnosis != null) {
                Communicator c = new Communicator("localhost", 5555);
                if (c.remoteClassify(diagnosis, 5)) {
        %>
        <table class="results">
            <tr>
                <th>Kód</th>
                <th>Bizonyosság</th>
            </tr>
            <%
                int i=0;
                for (Result r : c.getResults()) {
                    ++i;
                    out.println(String.format("<tr class=\"%s\">",(i%2==0)?"even":"odd"));
            %>
                <td><%=r.code%></td>
                <td><%=r.confidence%></td>
            </tr>        
            <%
                }
            %>
        </table>
        <%
        } else { //error
        %>
        <div class="errorMessage">Hiba történt a feldolgozás során:<br /><%=c.getErrorMessage()%></div>
        <%
                }
            }


        %>
    </body>
</html>
