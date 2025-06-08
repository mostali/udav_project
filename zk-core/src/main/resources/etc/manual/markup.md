[HOME](http://zznote.ru/_manual)
---  
### Простая подстановка (по имени, странице, поддомену)
```
@{{NOTE}}                          
@{{/page/NOTE}}                    
@{{subdomain/page/NOTE}}           
```


### Подстановка ключей из url
```
#Мапа ключей строится по префиксу '$$', например так url?$$mykey=myvalue
#return myvalue
${{mykey}}
```

### Вывод значений из application.properties
```
#{{app.prop.key}}
```

### Вывод значений из дерева GlobalContext
```
#Used GNC tree for store value
&{{myKey}}
```

### Вывод данных от выводов OK,ERR,LOG
```
@{{NOTE}}{{{&}}}}           -- &{{NOTE}}
@{{NOTE}}{{{&&}}}}         -- &&{{NOTE}}
@{{NOTE}}{{{&&&}}}}       -- &&&{{NOTE}}
```

## Доступ к строке и файлу
### Вывод значения по номеру строки
```
@{{NOTE}}{{{::1}}}}
```

### Вывод значения из файла
```
@{{NOTE}}{{{file:/file}}}}
```

## Deprecated
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
