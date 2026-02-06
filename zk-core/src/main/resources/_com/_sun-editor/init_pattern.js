SUNEDITOR.create('%s', {
    toolbarContainer : '#toolbar_container',
    showPathLabel : false,
    charCounter : true,
    maxCharCount : 720,
    width : 'auto',
    maxWidth : '700px',
    height : 'auto',
    minHeight : '100px',
    maxHeight: '250px',
    buttonList : [
        ['undo', 'redo', 'font', 'fontSize', 'formatBlock'],
        ['bold', 'underline', 'italic', 'strike', 'subscript', 'superscript', 'removeFormat'],
        '/' // Line break
        ['fontColor', 'hiliteColor', 'outdent', 'indent', 'align', 'horizontalRule', 'list', 'table'],
        ['link', 'image', 'video', 'fullScreen', 'showBlocks', 'codeView', 'preview', 'print', 'save']
    ],
    callBackSave : function (contents, isChanged) {
        console.log(contents);
    }
});