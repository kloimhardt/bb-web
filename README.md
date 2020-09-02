# bb-web
Using the Clojure Interpreter Babashka for client side scripting

## Run project
Although written in the Clojure language, no Clojure installation is needed. Instead, Babashka drives this small client-server application. Make sure you have a version of [Babashka with httpkit](https://github.com/borkdude/babashka/issues/556) installed. Open a terminal window and type: 

    bb server.clj

Your web-browser will open on `localhost:8000` and show a single button.  If your terminal shows a `Could not find: org.httpkit.server` error, your currently installed Babashka does not support http-kit.

Click on the `hot reload` button and some new ones appear. Try them out and see Babashka in action on the client side.

To code your own ideas, edit the file `browser.cljs`. Maybe change the text that appears on top of the web-page. Press `hot reload` and see the changes.

You can also edit the file `server.clj`. Maybe change the greeting text. To see the effect, stop the server in the console by pressing `Ctrl+C` and restart with `bb server.clj`.

## Rationale of bb-web
It shows some of the good Clojure stuff: same language on the client and the server, Hiccup syntx, Reagents clean client state management, a glimpse of what hot reloading means.

It lowers the bar to Clojure Web-development. There is no involved installation process. Here I especially have Windows users with company laptops and no Admin rights in mind.

Of course, some power tools are not available. Especially Cljs-REPL, integrant/mount, hot-relod on file save. As powerful as those concepts are, they need to be mastered. And some of the lack is made up by Babashka's brisk start up time and the fact that it is an interpreter and not a compiler.

One valid objection to bb-web is: one does not need client-side scripting for small web-apps. One can do with server side rendering and bb-web is therefore unnecessary. I can only respond that state management on client side is more intuitive to some of us. Moreover, the big advantage of Clojure over Python, Ruby, PHP, Erlang is in Clojurescript, it often enters through the front end, and bb-web is a small door for this.


## Advanced: add Javascript libraries for use with Babashka

You need to install Clojure and Shadow-cljs to do this advanced step.

Only one ClojureScript file is behind the scenes of bb-web: ``js/src/bb_web/app.cljs``. It is 20 lines and compiles to 6MB Javascript code (`js/bb_web/bb_web.js` is pre-compiled in the repository). 

As you can see in the ``:require`` section of `app.cljs`, two libraries are made available for use with Babashka: Reagent and Ajax.

You can add additional libraries  to the `:require` section and add new functions to the ``:bindings`` map.

To compile, type:
    
    cd js
    npm init -y
    npm install shadow-cljs
    shadow-cljs compile bbjs
    cd ..
    bb server.clj


Of course, additional libraries will increase the initial 6MB size. 
