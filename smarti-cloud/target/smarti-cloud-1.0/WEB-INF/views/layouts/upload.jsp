<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page session="false" %>
<html>
	<head>
		<title>SMARTi Web</title>
		<link rel="stylesheet" href="<c:url value="/resources/css/base.css" />" type="text/css" media="screen" />
		<link rel="stylesheet" href="<c:url value="/resources/page.css" />" type="text/css" media="screen" />
		<link rel="stylesheet" href="<c:url value="/resources/form.css" />" type="text/css" media="screen" />
		<link rel="stylesheet" href="<c:url value="/resources/messages/messages.css" />" type="text/css" media="screen" />
 		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/images/pro_drop_1.css"/>" />
 		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/images/query.css"/>" />
		<link rel="stylesheet" href="<c:url value="/resources/css/jquery-ui.css"/>" type="text/css" media="screen" />
	<!-- 	<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" type="text/css" media="screen" />
 	-->
		<script src="<c:url value="/resources/stuHover.js"/>" type="text/javascript"></script>
	 	<script src="<c:url value="/resources/js/jquery-1.9.1.js"/>" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-ui.js"/>" type="text/javascript"></script>
    <script>


        function returnFileUrl() {

            var funcNum = "${funcNum}";
            var fileUrl = "${fileUrl}";
            window.parent.CKEDITOR.tools.callFunction( funcNum, fileUrl, function() {
                // Get the reference to a dialog window.
                var dialog = this.getDialog();
                // Check if this is the Image Properties dialog window.
                if ( dialog.getName() == 'image' ) {
                    // Get the reference to a text field that stores the "alt" attribute.
                    var element = dialog.getContentElement( 'info', 'txtAlt' );
                    // Assign the new value.
                    if ( element )
                        element.setValue( "${altTxt}" );
                }
                // Return "false" to stop further execution. In such case CKEditor will ignore the second argument ("fileUrl")
                // and the "onSelect" function assigned to the button that called the file manager (if defined).
                // return false;
            } );
            window.close();
        }
    </script>

	</head>
	<body>

    <button onclick="returnFileUrl()">Select File</button>

	</body>
</html>