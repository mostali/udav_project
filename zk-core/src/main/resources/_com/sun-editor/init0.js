 sed = SUNEDITOR.create('{{se_id}}', {
   templates: [
           {
               name: 'Template-1',
               html: '<p>HTML source1</p>'
           },
           {
               name: 'Template-2',
               html: '<p>HTML source2</p>'
           },
           {
              name: 'Template-3',
              html: '<h2>HTML</h2> source2'
           }
       ],
    buttonList: [
        ['undo', 'redo'],
                [':p-More Paragraph-default.more_paragraph', 'font', 'fontSize', 'formatBlock', 'paragraphStyle', 'blockquote'],
                ['bold', 'underline', 'italic', 'strike', 'subscript', 'superscript'],
                ['fontColor', 'hiliteColor', 'textStyle'],
                ['removeFormat'],
                ['outdent', 'indent'],
                ['align', 'horizontalRule', 'list', 'lineHeight'],
                ['-right', ':i-More Misc-default.more_vertical', 'fullScreen', 'showBlocks', 'codeView', 'preview', 'print', 'save', 'template'],
                ['-right', ':r-More Rich-default.more_plus', 'table', 'imageGallery'],
                ['-right', 'image', 'video', 'audio', 'link']
    ],
    callBackSave : function (contents, isChanged) {
        //console.log(isChanged+":"+contents);
        if(isChanged){
            zAu.send(new zk.Event(zk.Widget.$('#{{se_id}}'), 'onSave', {'html': contents }, {toServer:true}));
        }
    }
})
