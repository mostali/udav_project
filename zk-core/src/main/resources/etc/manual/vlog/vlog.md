⚒ Запилил Docker API

```declarative
http://q.com:8080/_aci/    #вывод версии установленной Docker / 24.0.2
http://q.com:8080/_aci/build/ab:3/home/dav/pjm/glt/dr.Ob.Dockerfile    #собрать Docker-image по указанному пути
http://q.com:8080/_aci/create/ab3/ab:3/8083:8080/java/-jar/beaapp.jar    #запустить Docker-container по указанным и выполнить shell script
http://q.com:8080/_aci/start/CID     #Старт Docker-container по ID
http://q.com:8080/_aci/logs/CID    #Посмотреть вывод выполняющегося Docker-container
http://q.com:8080/_aci/stop/CID     #Остановка Docker-container
http://q.com:8080/_aci/state/CID     #Текущее состояние Docker-container 
http://q.com:8080/_aci/rm/CID    #Удаление Docker-container
```

❂ Благодаря этому стала доступна возможность запускать отдельные инстансы XNode со своим пространством, а также собирать и запускать любые Docker образы и контайнеры


⚒ Запилено Tree API

```declarative
http://q.com:8080/_ati/!/page/item?v=val    #Сохранить value
http://q.com:8080/_ati/*/page/item    #Посмотреть все ключи
```

❂ Позволяет на заметке иметь свое key/value хранилище



ATI TreeApi
