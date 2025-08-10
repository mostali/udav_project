const editorImageSample = SUNEDITOR.create('sample', {
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
            console.log(isChanged+":"+contents);
    }
})


const editorImageSample = SUNEDITOR.create('sample', {
    buttonList: [
        ['undo', 'redo'],
                [':p-More Paragraph-default.more_paragraph', 'font', 'fontSize', 'formatBlock', 'paragraphStyle', 'blockquote'],
                ['bold', 'underline', 'italic', 'strike', 'subscript', 'superscript'],
                ['fontColor', 'hiliteColor', 'textStyle'],
                ['removeFormat'],
                ['outdent', 'indent'],
                ['align', 'horizontalRule', 'list', 'lineHeight'],
                ['-right', ':i-More Misc-default.more_vertical', 'fullScreen', 'showBlocks', 'codeView', 'preview', 'print', 'save', 'template'],
                ['-right', ':r-More Rich-default.more_plus', 'table', 'math', 'imageGallery'],
                ['-right', 'image', 'video', 'audio', 'link']
    ],
})

SUNEDITOR.create('sample',{
     callBackSave : function (contents, isChanged) {
        console.log(contents);
    }
})

var editorimageResize = SUNEDITOR.create('sample', {
    buttonList: [
        ['undo', 'redo'],
        ['image']
    ],
})

const editorImageSample = SUNEDITOR.create('sample', {
    buttonList: [
        ['undo', 'redo'],
        ['formatBlock'],
        ['horizontalRule', 'list', 'table'],
        ['image', 'video'],
        ['showBlocks', 'fullScreen', 'preview', 'print']
    ],
})