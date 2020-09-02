# bb-web
Babashka Clojure Interpreter used full-stack in browser and backend
## run project
make sure you have a version of [babashka with httpkit](https://github.com/borkdude/babashka/issues/556) installed otherwise you get an error:
----- Error --------------------------------------------------------------------
Type:     java.lang.Exception
Message:  Could not find namespace: org.httpkit.server.

to start, type:
bb server.clj

## build bb_web.js with shadow-cljs
you need npm installed

cd js
npm init -y
npm install shadow-cljs
shadow-cljs compile bbjs
cd ..
bb server.clj
