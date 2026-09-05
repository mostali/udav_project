function initWatchSwipeById(id) {
    //https://stackoverflow.com/questions/2264072/detect-a-finger-swipe-through-javascript-on-the-iphone-and-android
    let el=document.getElementById(id);
    el.addEventListener('mousedown', handleTouchStart, false);
    window.addEventListener('mouseup', handleTouchEnd, false);

    var xDown = null;
    var yDown = null;
    var startDate = null;

    function handleTouchStart(evt) {
        xDown = evt.clientX;
        yDown = evt.clientY;
        startDate=new Date();
    };

    function handleTouchEnd(evt) {
        if ( ! xDown || ! yDown ) {
            return;
        }
        let d=7;
        let x = evt.clientX;
        let y = evt.clientY;
        let dx = xDown - x;
        let dy = yDown - y;
//        console.log("x:"+x+",y:"+y+", dx:"+dx+",dy:"+dy);
        let trbl=[0,0,0]//trbl
        if(Math.abs(dy)>d)trbl[0]=dy;//dy>0?1:-1;
        if(Math.abs(dx)>d)trbl[1]=dx;//dx>0?-1:1;

        trbl[2]=new Date().getTime() - startDate.getTime();

        xDown = null;
        yDown = null;
        startDate=null;

        //console.log("info",trbl);
        //console.log("rect",el.getBoundingClientRect());

        zAu.send(new zk.Event(zk.Widget.$('#'+id), 'onClickInfo', {'info': trbl,'rect': el.getBoundingClientRect() }, {toServer:true}));
    };

}

