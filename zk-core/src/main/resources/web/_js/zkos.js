
function p(o){console.log(o);}
function p_(){console.log(arguments);}
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

function i(id){return document.getElementById(id);}
function el(sel){return document.querySelector(sel);}
function els(sel){return document.querySelectorAll(sel);}
function elAbsY(sel){return el(sel).getBoundingClientRect().top - document.body.getBoundingClientRect().top;}
function isInViewport(t){let e=el(t).getBoundingClientRect();return e.top>=0&&e.left>=0&&e.bottom<=(window.innerHeight||document.documentElement.clientHeight)&&e.right<=(window.innerWidth||document.documentElement.clientWidth)}//https://www.javascripttutorial.net/dom/css/check-if-an-element-is-visible-in-the-viewport/
function copyToClb(e){if(navigator.clipboard&&window.isSecureContext)return navigator.clipboard.writeText(e);{let t=document.createElement("textarea");return t.value=e,t.style.position="fixed",t.style.left="-999999px",t.style.top="-999999px",document.body.appendChild(t),t.focus(),t.select(),new Promise((e,o)=>{document.execCommand("copy")?e():o(),t.remove()})}}//https://stackoverflow.com/questions/51805395/navigator-clipboard-is-undefined
function copyToClbFrom(elFinder){var el=elFinder();if (el&&el.innerHTML)copyToClb(el.innerHTML)}
function toggleFullScreen() {!document.fullscreenElement?document.documentElement.requestFullscreen():(document.exitFullscreen?document.exitFullscreen():console.log('Fullscreen command not apply'))}

function pathname(index){ var path=location.pathname.split('/'); path.shift(); return path[index]; }

function highlightElement(id, duration) {
    const element = document.getElementById(id);
    if (!element) {
        console.error("Element with id '"+id+"' not found.");
        return;
    }
    // Устанавливаем начальное состояние подсветки
    element.style.transition = 'background 1.0s ease-in-out';
    element.style.background = 'linear-gradient(90deg, rgba(255, 255, 0, 0.0), rgba(255, 255, 0, 0.96))';
    element.style.transform = 'scale(1.5)';
     // Убираем подсветку через заданное время
    setTimeout(() => {
        element.style.transition = 'background 1.0s ease-in-out';
        element.style.background = 'transparent';
        element.style.transform = 'scale(1)';
    }, duration);
}

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

// Пример использования:
// toggleOpacity('#myElement', 2000); // Удаляет opacity на 2 секунды

function toggleOpacity(sel, ms) {
    // Находим элемент по селектору
    const element = document.querySelector(sel);

    // Проверяем, существует ли элемент
    if (!element) {
        console.error('Элемент не найден по селектору:', sel);
        return;
    }

    // Сохраняем текущее значение opacity
    const originalOpacity = window.getComputedStyle(element).opacity;

    // Удаляем свойство opacity
    element.style.opacity = '';

    // Устанавливаем таймер для восстановления opacity
    setTimeout(() => {
        element.style.opacity = originalOpacity;
    }, ms);
}



// Пример использования
//const strings = ['Hello', 'World', 'JavaScript', 'is', 'awesome'];
//const screen = [10, 10, 800, 600]; // x, y, w, h
//const step = 50; // Или -1 для случайного размещения
//
//distributeStringsOnScreen(strings, screen, step);

function distributeStringsOnScreen(strings, screen, step) {
    step = step || -1;
    const [x, y, w, h] = screen || [0, 0, window.innerWidth, window.innerHeight];
    const positions = [];

    // Удаляем все существующие элементы с экрана
    const existingElements = document.querySelectorAll('.distributed-string');
    existingElements.forEach(element => element.remove());

    if (step === -1||step===null) {
       console.log("random pos");
        // Рандомное распределение
        strings.forEach(string => {
            const randomX = Math.floor(Math.random() * (w - 10)) + x; // 100 - пример ширины строки
            const randomY = Math.floor(Math.random() * (h - 10)) + y; // 30 - пример высоты строки
            positions.push({ string, x: randomX, y: randomY });
        });
    } else {
       console.log("eq pos");
        // Равномерное распределение
        const cols = Math.floor(w / step);
        const rows = Math.ceil(strings.length / cols);

        for (let i = 0; i < strings.length; i++) {
            const row = Math.floor(i / cols);
            const col = i % cols;

            const posX = x + col * step;
            const posY = y + row * step;

            positions.push({ string: strings[i], x: posX, y: posY });
        }
    }

    // Создаем элементы и назначаем события клика
    positions.forEach(({ string, x, y }) => {
        const element = document.createElement('div');
        element.className = 'distributed-string'; // Добавляем класс для удаления
        if(Array.isArray(string)){
            element.title=string[0];
            string=string[1];
        }
        element.style.position = 'absolute';
        element.style.left = x + 'px';
        element.style.fontSize = "25px";
        element.style.top = y + 'px';
        element.style.zIndex = 9999;
        element.style.background = 'white';
        element.style.padding = '20px';
        element.innerText = string;
        if(string.length==7 && string.startsWith('#')){
            element.style.width = 100 + 'px';
            element.style.height = 100 + 'px';
            element.style.background = string;
        }

        // Назначаем событие клика
        element.addEventListener('dblclick', () => {
            copyToClb(string);
            // Удаляем все элементы со страницы
            const allElements = document.querySelectorAll('.distributed-string');
            allElements.forEach(el => el.remove());
        });

        document.body.appendChild(element);
    });
}



