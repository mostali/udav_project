
//window.addEventListener('scroll', function() { zk.log(pageYOffset + 'px');});

//document.getElementById("sample").addEventListener('mouseup', (e)=>{console.log(e)}, false);

window.addEventListener('mousedown', handleTouchStart, false);
window.addEventListener('mouseup', handleTouchEnd, false);

var xDown = null;
var yDown = null;

function handleTouchStart(evt) {
//    console.log("start");
    xDown = evt.clientX;
    yDown = evt.clientY;
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
    console.log("x:"+x+",y:"+y+", dx:"+dx+",dy:"+dy);
    let trbl=[0,0]//trbl
    if(Math.abs(dy)>d)trbl[0]=dy>0?1:-1;
    if(Math.abs(dx)>d)trbl[1]=dx>0?-1:1;
//    if(false){
//    if ( Math.abs( dx ) > Math.abs( dy ) ) {
//                if ( dx > 0 ) {
//                    console.log("left");
//                } else {
//                    console.log("right");
//                }
//            } else {
//                if ( dy > 0 ) {
//                    console.log("up");
//                } else {
//                    console.log("down");
//                }
//            }
//            }
    xDown = null;
    yDown = null;

    console.log(trbl);
};