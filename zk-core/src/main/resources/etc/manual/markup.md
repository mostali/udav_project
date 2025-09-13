[HOME](http://zznote.ru/_manual)


# Общее
- При вызове заметки через exe - заметка может обогащатся свойствами из различных источников, например другой заметки или из встроенной БД (см.ниже)

## ⚡ Основные 
---  
### Заметка подставляет данные из другой заметки (по имени, странице, поддомену) с помощью плэйсхолдера:
```
#root domain and index page
@{{NOTE}}                  
#root domain and page        
@{{/page/node}}                    
#subdomain, page and node        
@{{subdomain/page/note}}           
```


### Вывод значений из дерева GlobalContext
```
#Used GNC tree for store value (from main menu)
%{{myKey}}
```
l
### Вывод значений из application.properties
```
#{{app.prop.key}}
```


### Подстановка данных от выводов заметки OK,ERR,LOG
```
#OK
@{{NOTE}}{{{&}}}}
#ERR
@{{NOTE}}{{{&&}}}}
#LOG
@{{NOTE}}{{{&&&}}}}
```

### Подстановка ключей из url при REST запросе с ключом exe
- Для использования контекста запроса из url query используется плэйсхолдер:
```
${{queryArg}}
```
- Ключи берутся из url?queryArg=myvalue


## 📙 Доступ к строке и файлу

### Вывод значения по номеру строки
```
@{{NOTE}}{{{::1}}}}
```

### Вывод значения из файла
```
@{{NOTE}}{{{file:/file}}}}
```

## ❗ Deprecated
### Вывод значения по ключу (not work)

```
@{{NOTE}}{{{treeKey}}}}
```

## Not used ???

```
~{{/file}}
```

---
[HOME](http://zznote.ru/_manual)
