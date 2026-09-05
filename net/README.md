
# Java Net Utilities - Работа с сетью

## 🌐 Подключенные библиотеки
- Парсим HTML (https://jsoup.org/)  
- Http Client's (https://hc.apache.org/httpcomponents-client-ga/ & https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/)
- Создаем & парсим jwt-токены (https://github.com/jwtk/jjwt)

## 📚 Описание
- Работаем с http-запросами, парсим html, jwt
- содержит разные api для работы (nexus, gitlab, translate,etc)

## ✅ Основные классы
[AConn](src%2Fmain%2Fjava%2Fudav_net_client%2FAConn.java) - Сервис http-запросов (ApacheHttpClient)      
[OkConn](src%2Fmain%2Fjava%2Fudav_net_client%2FOkConn.java) - Сервис http-запросов (OkHttpClient)  

[QueryUrl](src%2Fmain%2Fjava%2Fudav_net%2Fquery%2FQueryUrl.java) - Парсим url query  

[UJwt](src%2Fmain%2Fjava%2Fudav_jwt%2FUJwt.java) - Парсим JWT  


## ✅ HttpClient
[AbsNetRsp](..%2Futl%2Fsrc%2Fmain%2Fjava%2Fmpc%2Fnet%2FAbsNetRsp.java) - Абстрактный Response for HttpClient (ApacheClient,OkClient,RetroClient)  
[ARsp](src/main/java/udav_net_client/ARsp.java) - impl ApacheHttpClient  
[OkRsp](src%2Fmain%2Fjava%2Fudav_net_client%2FOkRsp.java) - impl OkHttpClient  
[RRsp](..%2Fapps%2Fapp_tsm%2Fsrc%2Fmain%2Fjava%2Futl_retro%2FRRsp.java) - impl RetroHttpClient


## ✅ Дополнительные фичи
- [UJsoup](src%2Fmain%2Fjava%2Fudav_net%2FUJsoup.java) - парсим Html
- [UNexus](src%2Fmain%2Fjava%2Fudav_net%2Fapis%2FUNexus.java) - Nexus API  
- [UGitLab](src%2Fmain%2Fjava%2Fudav_net%2Fapis%2FUGitLab.java) - Gitlab API  
- [M2Repo](src%2Fmain%2Fjava%2Fudav_net_exp%2Fm2_repo%2FM2Repo.java) - Простая реализация для работы с maven-репозитариями  
- [ULibreTranslator](src%2Fmain%2Fjava%2Fudav_net_exp%2Ftranslator%2FULibreTranslator.java) - Запускаем свой инстанс Libre Translator  
- [UploadFile2DomainRequest](src%2Fmain%2Fjava%2Fudav_net_exp%2Fuploader_to_phpserver%2FUploadFile2DomainRequest.java) - Грузим файлы на PHP сервер  