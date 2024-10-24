
function p(){console.log(arguments);}
function e(){console.error(arguments);}
//function isset(arg) {return !isund(arg) && !isnull(arg);}
//function isund(arg) {return arg === undefined;}
//function isnull(arg) {return arg === null;}
//function isemp(arg) {return isund(arg) || isnull(arg) || arg.length == 0;}
//function hasop(obj,prop) {return isset(obj)&&isset(prop)&&obj.hasOwnProperty(prop);}

function isnum(n) {return !isNaN(parseFloat(n)) && isFinite(n);}
function isstr(s) {return (typeof s === 'string' || s instanceof String);}
function isarr(s) {return (typeof s === 'array' || s instanceof Array);}
function isjson(str) {try {JSON.parse(str);} catch (e) {return false;}return true;}

function el(sel){return document.querySelector(sel);}
function els(sel){return document.querySelectorAll(sel);}
function elAbsY(sel){return el(sel).getBoundingClientRect().top - document.body.getBoundingClientRect().top;}
function isInViewport(t){let e=el(t).getBoundingClientRect();return e.top>=0&&e.left>=0&&e.bottom<=(window.innerHeight||document.documentElement.clientHeight)&&e.right<=(window.innerWidth||document.documentElement.clientWidth)}//https://www.javascripttutorial.net/dom/css/check-if-an-element-is-visible-in-the-viewport/
function copyToClb(e){if(navigator.clipboard&&window.isSecureContext)return navigator.clipboard.writeText(e);{let t=document.createElement("textarea");return t.value=e,t.style.position="fixed",t.style.left="-999999px",t.style.top="-999999px",document.body.appendChild(t),t.focus(),t.select(),new Promise((e,o)=>{document.execCommand("copy")?e():o(),t.remove()})}}//https://stackoverflow.com/questions/51805395/navigator-clipboard-is-undefined
function copyToClbFrom(elFinder){var el=elFinder();if (el&&el.innerHTML)copyToClb(el.innerHTML)}
function toggleFullScreen() {!document.fullscreenElement?document.documentElement.requestFullscreen():(document.exitFullscreen?document.exitFullscreen():console.log('Fullscreen command not apply'))}

function pathname(index){ var path=location.pathname.split('/'); path.shift(); return path[index]; }

//Global Utility
class U{
    static hasop (obj,prop) {return obj&&prop&&obj.hasOwnProperty(prop);}
    static hasopnn (obj,prop) {return obj&&prop&&obj.hasOwnProperty(prop)&&obj[prop]!=null;}
    static isd (o) {return o !== undefined;}
    static isund (o) {return o === undefined;}
    static p (o) { var a=arguments;var l=a.length;var m=l==0?console.log(a):( l==1?console.log(a[0]):( l==2?console.log(a[0],a[1]):console.log(a) ) ); }
    static e (o) { var a=arguments;var l=a.length;var m=l==0?console.error(a):( l==1?console.error(a[0]):( l==2?console.error(a[0],a[1]):console.error(a) ) ); }
}
class UN{
    static DBL(vl){var n=Number(vl);return n;}
    static DBL2(vl){var n=Number(vl);return n?n.toFixed(2):n;}
}
 //Global Array Utility
 class AR{
    static first (a) {return a[0];}
    static last (a) {return a[a.length-1];}
 }


