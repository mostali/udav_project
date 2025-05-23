# Code Style & Development
Написанное ниже - личное мнение, как разработчика Java, сложившееся после многолетней практики написания ПО на Java

## switch/case it is good
Всегда, когда позволяет типизация - использовать switch/case
- enum - часто идеальный кандидат, когда нет необходимости в динамических наследниках
- код становится красивым и хорошо структурированным
- ключевые enum-ы приложения задают каркас(модель) приложения
- значительно помогает разработчику ориентироваться в бизнесе приложения
- разделяет бизнес от операционных задач на уровне кода

#### Pare<K,V> отличный помощник на этапе прототипирования и рефакторинга
- экономия времени на создании/поддержки классов моделей, 
- уберегает от создания/поддержки/рефакторинга лишних связей
- Pare<,,> легко читаем - и легко рефакторится в класс (но не наоборот) 
- просто расширяется за счет наследников Pare3,4

#### Реактивные модели или Business Transformer's - зло 
- бизнесовые "трансформеры" ломают целостность кода за счет потери ссылок на данные и сложно реализуемых конвертаций(часто требующих бизнес-состояния приложения)
- избегать мапинги, set/get ( лучше инкапсулировать/агрегировать )
- в идеале все модели приложения инкапсулируются в общую модель (модуль/сервис)
- ленивая инициализация данных модели (реактивные модели) - позволит не инициализировать лишнее, избежать зависимостей и не предвиденных ошибок

#### *Util's - ~~зло~~ хорошо ?
-  *Utils хорошо - когда это действительно *Utils - остальное сервисы
- выносить все что выносится в *Utils 
- толстые *Utils структурируются, формируя *BusinessService-ы

#### Local Persistence Map (Tree)
- локальные персистентные хранилища приложения типа Key/Value могут просто решать сложные задачи, требующих персистентности для различных состояний и компонентов приложения 
- избавляет от написания и поддержки моделей, сводя работу с данными к тривиальным и понятным crud-операциям
- UTree/UTreeNext/UtreeCacheLife - как пример Key/Value хранилища на SQLite

## Прятать модели с данными за контракты
- избавляет от дублирования и поддержки разных версий моделей, особенно на начальном этапе создания прототипа/mvp
- использовать простые сплитеры (Cmd7) и легкие контракты ( MapTableContract)  - как и с Pare они легко выделяются в свои типы (но не наоборот)

## Правильные наименования - как часть документации кода
- боль с придумыванием имени переменной в общем случае решается тремя вопросами - Что это? Что делает? Для чего(внешние связи) ?
- т.е. имена переменных содержат ТИП, ФУНКЦИОНАЛЬНОСТЬ, ЦЕЛЬ(по отношению к чему)
Например, applierMenu, readerFile или APK_GS_KEY_PATH
- названия переменных, методов, классов - как правило, всегда должны называться позитивно, т.е. избегать использование частиц 'не' или негативных подходов (через исключения). Отрицание может значительно усложнить понимание кода, поскольку имеет двоякое семантическое толкование 'не' (т.е. 'НЕ НЕ делать' - т.е. ДА делать | 'НЕ НЕ делать' - т.е. не выполнять совсем )
- для описания какой-либо функциональности программы - использовать позитивные ветки сценариев метода. Может позволить избежать сложностей и ошибок при расширении функционала, т.е. при добавлении новых сценариев

## Вынос переменных в верхний блок
- выделять отдельные блоки для кода с инициализцией данных (head) и логикой для работы с этими данными (body)
- код структурируется, становится целостным, легче рефакторится, видно за что отвечает и где цепляются переменные
- подобная практика с выделением head/body областей с кодом является стандартом де-факто и проявляется во многих аспектах программирования 

## Выносить вызовы методов из return в отдельные ссылки
- Повышается читаемость, видно возвращаемый тип.
- позволяет удобно останавливать отладчик, смотреть/изменять данные
- особенно при return 
```shell
//ok
Type returnObj=callMethod();
return returnObj;
```
```shell
//!ok
return callMethod();
```


---  

--- 

--- 

# Дзен питона   

- Красивое лучше, чем уродливое.  
- Явное лучше, чем неявное.  
- Простое лучше, чем сложное.  
- Сложное лучше, чем запутанное.  
- Плоское лучше, чем вложенное.  
- Разреженное лучше, чем плотное.  
- Читаемость имеет значение.  
- Особые случаи не настолько особые, чтобы нарушать правила.  
- При этом практичность важнее безупречности.  
- Ошибки никогда не должны замалчиваться.  
- Если они не замалчиваются явно.  
- Встретив двусмысленность, отбрось искушение угадать.  
- Должен существовать один и, желательно, только один очевидный способ сделать это.  
- Хотя он поначалу может быть и не очевиден, если вы не голландец [^1].  
- Сейчас лучше, чем никогда.  
- Хотя никогда зачастую лучше, чем прямо сейчас.  
- Если реализацию сложно объяснить — идея плоха.  
- Если реализацию легко объяснить — идея, возможно, хороша.  
- Пространства имён — отличная штука! Будем делать их больше!  


# Other
###  <u>YAGNI</u>  You Aren’t Gonna Need It / Вам это не понадобится 
### <u>DRY</u> Don’t Repeat Yourself / Не повторяйтесь
### <u>KISS</u> Keep It Simple, Stupid / Будь проще
### <u>SOLID</u>
#### <u>S</u> | Single-responsibility principle /Принцип единственной ответственности
#### <u>O</u> | Open–closed principle / Принцип открытости-закрытости
#### <u>L</u> | Liskov substitution principle / Принцип подстановки Лисков
#### <u>I</u> | Interface segregation principle / Принцип разделения интерфейсов
#### <u>D</u> | Dependency inversion principle / Принцип инверсии зависимостей
### <u>Avoid | Premature Optimization</u> 
Избегайте преждевременной оптимизации

### <u>Бритва Оккама</u> 
Не следует множить сущее без необходимости / Не следует привлекать новые сущности без крайней на то необходимости
### <u>Big Design Up Front</u> 
Глобальное проектирование прежде всего


## Другие ссылки
- Стандарты кодирования (см. Стандарты кодирования)
  https://codex.wordpress.org/Category:Russian_Codex
