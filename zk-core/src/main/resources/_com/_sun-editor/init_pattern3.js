SUNEDITOR.create('{se_id}', {
    font : [
        'Arial',
        'tohoma',
        'Courier New,Courier'
    ],
    fontSize : [
        8, 10, 14, 18, 24, 36
    ],
    colorList : [
        ['#ccc', '#dedede', 'OrangeRed', 'Orange', 'RoyalBlue', 'SaddleBrown'],
        ['SlateGray', 'BurlyWood', 'DeepPink', 'FireBrick', 'Gold', 'SeaGreen'],
    ],
    width : '100%',
    maxWidth : '600px',
    minWidth : '400px',
    height : 'auto',
    videoWidth : '80%',
    youtubeQuery : 'autoplay=1&mute=1&enablejsapi=1',
    popupDisplay : 'local',
    resizingBar : false,
    buttonList : [
            'font', 'fontSize',
            'fontColor', 'hiliteColor', 'video',
            'fullScreen', 'codeView',
             'preview', 'save'],
    callBackSave: function (contents, isChanged) {
        alert(contents);
    }
});