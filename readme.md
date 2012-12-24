Ancient Book
============

Simple book manager

Команды
-------

`/abook save [-d data] [-a "cool author"] [-t title]` - создать (или обновить) шаблон книги с заданным полем data из той, что в руках.  
Если параметры не заданы - они берутся из книги в руке. Название и автора с пробелами нужно заключать в "". В них можно использовать цветные коды  
`/abook remove <data>` - удалить книгу с заданным data  
`/abook list` - выводит список книг  
`/abook give <data> <player> [is_unsigned]` - создать и выдать книгу с заданным data. Если последний параметр true - неподписанную. Если книга не помещается в инвентарь, она будет дропнута поблизости  
`/abook reload` - перегрузить конфиг, файлы языка и файл книг  
`/abook unsign` - "отписать" книгу в руке  

Пермишены
---------

`ancientbook.command` - доступ ко всем командам (op)

Download
--------

Версия Bukkit: 1.4.6-R0.1  
https://dl.dropbox.com/u/14150510/dd/mccity/AncientBook.jar