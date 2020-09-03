# bb-web
Using the Clojure interpreter Babashka for scripting a Web-GUI.

## Run project
This small client-server demo is written in Clojure. The main feature is that no Clojure installation is needed. Nevertheless you can live code the Web-GUI yourself. 

Make sure you have a version of [Babashka with httpkit](https://github.com/borkdude/babashka/issues/556) on your computer, it is just a single executable file with the two letter name `bb`. Having that in place, open a console window, change into your bb-web directory and type: 

    bb server.clj

Your web-browser will open and show some buttons. Try them out and see Babashka in action on the client side.

 If you see the `Could not find: org.httpkit.server` error, your local Babashka does not support http-kit. Again, get one that does.

To code your own ideas, edit the file `client.cljs`. Maybe change the text that is displayed on top of the web-page. Press `hot reload` and see the changes.

You can also edit the file `server.clj`. Maybe change the server greeting text. To see the effect, stop the server in the console by pressing `Ctrl+C` and restart with `bb server.clj`.

## Rationale of bb-web
It shows some of the good Clojure stuff: same language on the client and the server, Hiccup syntax, Reagent's clean client state management, hot reloading (at least a glimpse).

It lowers the bar to Clojure Web-development. There is no involved installation process. Here I especially have Windows users with company laptops and no Admin rights in mind.

Of course, some power tools are not available. Especially Cljs-REPL or integrant/mount. As powerful as those concepts are, they first need to be mastered. And some of the lack is made up by Babashka's brisk start up time.

One valid objection to bb-web is: one does not need client-side scripting for small web-apps, server side rendering is sufficient. I can only respond that state management on client side is more intuitive to some of us. Moreover, a big advantage of Clojure over Python, Ruby, PHP, Erlang is in Clojurescript, and bb-web is a door leading there.


## Advanced: add Javascript libraries for use with Babashka

You need to install Clojure and Shadow-cljs to do this advanced step.

Only one ClojureScript file is behind the scenes of bb-web: ``js/src/bb_web/app.cljs``. It is 25 lines and compiles to 1MB Javascript code (`js/bb_web/bb_web.js` is pre-compiled in the repository). 

As you can see in the ``:require`` section of `app.cljs`, two libraries are made available for use with Babashka: Reagent and Ajax.

You can add additional libraries  to the `:require` section and add new functions to the ``:bindings`` map.

To compile, open a console window, change into your bb_web directory and type:
    
    cd js
    echo
    shadow-cljs release bbjs
    cd ..
    bb server.clj

Of course, additional libraries will increase the initial 1MB size. 

If you are compiling for the first time, instead of the (effectless) `echo` command above, type the following two lines:

    npm init -y
    npm install shadow-cljs

If you want the full shadow-cljs experience while editing, instead of `echo`, type `shadow-cljs watch cljs` and open `http://localhost:8081` in your browser. You will not see any buttons as the Babashka part is not loaded. Only the familiar bottom message text is there, maybe change it in `app.cljs`, save and watch shadows' hot reloading magic happen immediately.
