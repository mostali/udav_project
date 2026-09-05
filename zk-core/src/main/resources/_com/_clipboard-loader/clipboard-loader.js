


    function draw(canvas) {
      var context = canvas.getContext("2d");
      context.fillStyle = "#000000";
      context.fillRect(0, 0, 400, 300);
      context.fillStyle = "#ff0000";
      context.fillRect(50, 50, 300, 200);
    }

    var sidCounter = 0;

    function uploadCanvas(wgt, canvas) {
      var blob = zUtl.convertDataURLtoBlob(canvas.toDataURL('image/png'));
      uploadImage(wgt, blob);
    }

    function initPasteImage(wgt) {
      wgt.$n().addEventListener('paste', function(e) {
        // adapted from: https://stackoverflow.com/questions/6333814/how-does-the-paste-image-from-clipboard-functionality-work-in-gmail-and-google-c

        var items = (event.clipboardData  || event.originalEvent.clipboardData).items;
        console.log(JSON.stringify(items)); // will give you the mime types
        // find pasted image among pasted items
        var blob = null;
        for (var i = 0; i < items.length; i++) {
//          if (items[i].type.indexOf("image") === 0) {
            blob = items[i].getAsFile();
//          }
        }
        // load image if there is a pasted image
        if (blob !== null) {
          uploadImage(wgt, blob);
        } else {
          zk.error('no image item found in clipboard items');
        }
      });
    }

    function uploadImage(wgt, blob) {
      var formData = new FormData();
      var xhr = new XMLHttpRequest();
      var sid = sidCounter++;

      formData.append('file', blob);
      xhr.onload = function (e) {
        if (this.readyState === 4) {
          if (this.status === 200) {
            wgt.fire('onImageUpload', {sid: sid}, {toServer: true});
          } else {
            zk.error(xhr.statusText);
          }
        }
      };
      zk.UploadUtils.ajaxUpload(wgt, xhr, formData, sid);

    }
