@echo off
if exist bb.exe (
set bb=
) else (
set bb=Warning: please download bb.exe
)

if exist bb-web.exe (
set bbweb=
) else (
set bbweb=Warning: please download bb-web.exe
)

echo 1 Start %bb%
echo 2 Hot reload
echo 3 Luminus Guestbook minimal backend
echo 4 Luminus Guestbook rich backend %bbweb%
echo 5 Edit with parinfer-codemirror
echo.

set /p reply=press 1 to 5, any key to Exit: 
if  /i %reply%==1 goto one
if  /i %reply%==2 goto two
if  /i %reply%==3 goto three
if  /i %reply%==4 goto four
if  /i %reply%==5 goto five

goto :exit

:one
bb examples\start.clj
goto :exit

:two
bb examples/hot_reload.clj examples/hot_reload.cljs 
goto :exit

:three
bb examples/guestbook_1.clj examples/guestbook_1.cljs
goto :exit

:four
bb-web examples/guestbook_2.clj examples/guestbook_1.cljs
goto :exit

:five
bb-web -cp examples -m parinfer-codemirror
goto :exit

:exit
pause
