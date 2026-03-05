SUNEDITOR.create((document.getElementById('%s') || '%s'),{
    toolbarContainer : '%s',
    showPathLabel : true,
    buttonList : ['save'],
    callBackSave : function (contents, isChanged) {
            console.log(contents);
    }
});