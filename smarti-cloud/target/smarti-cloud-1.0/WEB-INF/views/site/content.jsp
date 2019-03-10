<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" session="false"  %>

<div>



        <div class="container">
        <div data-ajax-replace="/smarticloud/assets/sample_hospital.carousel" class="row" >
  
        </div>
        <div class="row">here</div>
        </div> 






</div>
<style>
<!--
#div1 {
    width: 350px;
    height: 70px;
    padding: 10px;
    border: 1px solid #aaaaaa;
-->
</style>



<p>Drag the W3Schools image into the rectangle:</p>

<div id="div1" ondrop="drop(event)" ondragover="allowDrop(event)"></div>
<br>
<img id="drag1" src="img_logo.gif" draggable="true" ondragstart="drag(event)" width="336" height="69">

<script>
function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
    ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
    ev.preventDefault();
    var data = ev.dataTransfer.getData("text");
    ev.target.appendChild(document.getElementById(data));
}

if ( CKEDITOR.env.ie && CKEDITOR.env.version < 9 )
	CKEDITOR.tools.enableHtml5Elements( document );

// The trick to keep the editor in the sample quite small
// unless user specified own height.
CKEDITOR.config.height = 350;
CKEDITOR.config.width = 'auto';

CKEDITOR.replace( 'editor', {
	filebrowserBrowseUrl: '${host}assets/${appPath}.html',
    filebrowserUploadUrl: '${host}assets${appPath}.upload'
} );	
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