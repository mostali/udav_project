//https://jin0.ru/shop/catalog/domains/?domain=mm&sort=price&category=firstlevel
//document.querySelectorAll('#__page > div > div > div > div > div')
var maxLen=6;
var nextScroll=function(){  cleanDomains (getAllLongZone(maxLen)) ;window.scrollTo(0, document.body.scrollHeight); return document.body.scrollHeight;  }
var nextScrollIf=function(startH, delay_ms, clb_end){ var startH2=nextScroll(); if (startH == startH2) { clb_end(); return; } else setTimeout(function(){nextScrollIf(startH2,delay_ms,clb_end)},delay_ms) }
function doScrollToFooter(clb_end){  nextScrollIf(0,2000,clb_end)  }
doScrollToFooter(function(){console.log('hi footer')});

function cleanDomains(divs){ divs.forEach((d)=> d.classList.length>1 && d.remove()) }

function getPageDomains(successOrFail){
    var els = getAllZone().filter((d)=>{
        if (!d.classList.length) return false;
        if (!d.classList[0].startsWith('DomainTile_')) return false;
        //return true;
        if(!checkDomainLength(d,maxLen))return false;
        if(d.classList.length==1)return true;
        var rslt = d.classList[1].startsWith(successOrFail?'DomainTile__status_succeed_':'DomainTile__status_failed_');
        return rslt;
    })
    console.log("found total "+ (successOrFail?"SUCCESS":"FAIL") + " domain:" + els.length)
    return els;
}
function getAllZone(){]
     var els = Array.from(document.querySelectorAll('#__page > div > div > div > div > div')).filter( (d)=>{
         if (!d.classList.length) return false;
         if (!d.classList[0].startsWith('DomainTile_')) return false;
         return true;
     })
     console.log("Total zones : " + els.length )
     return els;
}
function getAllLongZone(){
    function checkDomainLength(div,maxLen){var zone=div.getAttribute('data-zone');return zone.length <= maxLen;}
    var els = getAllZone().filter( (d)=>{
        return !checkDomainLength(d,maxLen);
    } )
    //    console.log("Found long zones : " + els.forEach((e)=>console.log(e.getAttribute("data-zone"))) )
    console.log("Found LONG zones : " + els.length)
    return els;
}
function getAllFailZone(){
     var els = getAllZone().filter( (d)=>{ return d.classList.length>1 && d.classList[1].startsWith('DomainTile__status_failed_'); } )
     console.log("Found FAIL zones : " + els.length)
     return els;
}





