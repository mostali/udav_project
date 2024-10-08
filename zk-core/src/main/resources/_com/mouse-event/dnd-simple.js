//https://www.w3schools.com/howto/howto_js_draggable.asp
function dragElementById(id) {
    dragElement(document.getElementById(id));

    function dragElement(elmnt) {

      var rectStart = null;
      var ox=0, oy=0;

      var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
      if (document.getElementById(elmnt.id + "header")) {
        // if present, the header is where you move the DIV from:
        document.getElementById(elmnt.id + "header").onmousedown = dragMouseDown;
      } else {
        // otherwise, move the DIV from anywhere inside the DIV:
        elmnt.onmousedown = dragMouseDown;
      }

    var isCtrl,isShift=false;

      function dragMouseDown(e) {
         isCtrl=e.ctrlKey||false;
         isShift=e.altKey||false;
        if(!isCtrl && !isShift)
            return;
        e = e || window.event;
        e.preventDefault();
        // get the mouse cursor position at startup:
        pos3 = e.clientX;
        pos4 = e.clientY;

        rectStart=elmnt.getBoundingClientRect();
        ox=pos3-rectStart.left;
        oy=pos4-rectStart.top;
        //console.log("ox:"+ox+", oy:"+oy);

        document.onmouseup = closeDragElement;
        // call a function whenever the cursor moves:
        document.onmousemove = elementDrag;
      }

      function elementDrag(e) {
        e = e || window.event;
        e.preventDefault();
        // calculate the new cursor position:
        pos1 = pos3 - e.clientX;
        pos2 = pos4 - e.clientY;
        pos3 = e.clientX;
        pos4 = e.clientY;
        // set the element's new position:
        //elmnt.style.top = (elmnt.offsetTop - pos2) + "px";
       //elmnt.style.left = (elmnt.offsetLeft - pos1) + "px";
      }

      function closeDragElement() {
        // stop moving when mouse button is released:
        document.onmouseup = null;
        document.onmousemove = null;
        send_pos();
      }

      function send_pos() {
          zAu.send(new zk.Event(zk.Widget.$('#'+elmnt.id), 'onChangeXY', {'x': pos3-ox,'y': pos4-oy , 'isCtrl': isCtrl,'isShift': isShift }, {toServer:true}));
      }
       function send_pos() {
                zAu.send(new zk.Event(zk.Widget.$('#'+elmnt.id), 'onChangeXY', {'x': pos3-ox,'y': pos4-oy , 'isCtrl': isCtrl,'isShift': isShift }, {toServer:true}));
            }

    }

}