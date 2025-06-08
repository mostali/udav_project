//video$mold$ =
/* audio.js

	Purpose:

	Description:

	History:
		Thu Mar 26 11:59:58     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
//function (out) {
//			out.push('<video width="320" height="240" controls>');
//			out.push('<source src="movie.mp4" type="video/mp4">');
//			out.push('Your browser does not support the video tag.');
//			out.push('</video>');

function(a){a.push("<video",this.domAttrs_(),">",this.domContent_(),"</video>")};

//	out.push('<video', this.domAttrs_(), '>', this.domContent_());
//	for (var w = this.firstChild; w; w = w.nextSibling)
//		w.redraw(out);
//	out.push('</video>')
//}

//function (out) {
//	var multiple_ = (this.multiple && this.multiple == "true") ? " multiple" : "";
//	var url_ = this.url.length > 0 ? this.url : "upload";
//
//	out.push('<form id="fileuploadform" method="POST" enctype="multipart/form-data">');
//
//	if (this.isScanDocumentDialog == "false") {
//        if (this.showDropzone == "true") {
//            out.push('<div id="dropzone" class="fade well">Поместите файл сюда для загрузки</div>');
//	    }
//        out.push('<div class="btnHolder">');
//	    if (this.showDropzone == "true") {
//            out.push('<div id="orSpan">или нажмите</div>');
//	    }
//		out.push('<div class="fileUpload btn">');
//		out.push('    <span>Загрузить</span>');
//		//out.push('<input id="', this.id, '" type="file" data-url="' , url_ ,'" name="files[]" ' ,
//		out.push('<input id="', this.id, '" type="file" name="files[]" ',
//			' class="upload ', this.getZclass(), '" ', multiple_, ' />');
//		out.push('</div>');
//		out.push('</div>');
//    }
//	out.push('    <div id="error" class="alert alert-danger" style="display:none;"/>');
//	if (this.showProgress == "true") {
//		out.push('<div id="progress" class="progress">');
//		out.push('<div class="bar" style="width: 0%;"></div>');
//		out.push('</div>');
//	}
//	if (this.showFileList == "true") {
//        if(this.maxNumberOfFiles == 1){
//            //out.push('Файл:');
//        } else {
//           out.push('<b>Файлы:</b>');
//        }
//        out.push('<div class="tableWrapper">');
//		out.push('<table id="uploaded-files" class="table">');
//		out.push('</table>');
//		out.push('</div>');
//	}
//	out.push('</form>');
//}