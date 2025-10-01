
## ZNote поддерживает несколько разновидностей REST API

### API для работы с заметками
#### Получение данных
```shell
#Получить содежимое заметки
GET http://zznote.ru/_api/mypage/*/mynote
```

#### Обновление данных

```shell
#Обновить содежимое заметки
GET http://zznote.ru/_api/mypage/!/mynote?v=note-value
```
```shell
#Обновить содежимое заметки
POST http://zznote.ru/_api/mypage/!/mynote
--#headers
myvalue
```



### API для работы с БД заметки (store by keyValue) (Experimental) 
```shell
#Положить в БД заметки mykey=myvalue
POST http://zznote.ru/_ati/mypage/mynote/put?k=mykey
--#headers
myvalue

```