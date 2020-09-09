# bb-web

Scripting small web apps with Babashka/SCI and Reagent

## Getting Started

The following single page demo is written in Clojure. You can live code its Web-GUI yourself. The main feature is that no Java+Clojurescript installation is needed to begin with. The only prerequisite is Babashka, a single executable file.

Download [Babashka with httpkit](https://github.com/borkdude/babashka/issues/556), a file with the two letter name `bb`. 

Then, download this repository via Github's `Code` button above, thereby creating a directory called `bb-web` or similar. Copy the `bb` executable into this directory and double click `bb_web_demo.bat`. Non MS-Windows(TM) users open a command prompt <sup>[1](#myfootnote1)</sup> and type:

    bb server.clj

 If you see the `Could not find: org.httpkit.server` error, by accident you are using a Babashka version that does not support http-kit.

If everything works as expected, your web-browser will open and show some buttons. Try them out and see Babashka in action on the client side.

To code your own ideas, edit the file `client.cljs`. Maybe change the text that is displayed on top of the web-page. Press `hot reload` and see the changes (or a nice error message if a sytax error should occur). Also press the browser reload button and notice, as opposed to `hot reload`, the resetting of the displayed number.

You can also edit the file `server.clj`. Maybe change the server greeting text. To see the effect, stop the server pressing `Ctrl+C` and restarting with the familiar `bb server.clj`.

## Further example

### Guestbook

    bb examples/guestbook_1.clj examples/guestbook_1.cljs

## Rationale of bb-web
Babashka (or rather SCI) displays nice error messages. They are more readable than, say, those of self hosted Clojurescript.

It shows some of the good Clojure stuff: same language on the client and the server, Hiccup syntax, Reagent's clean client state management, hot reloading (at least a glimpse).

It offers a low entry bar to Web-development. There is no involved installation process. I especially have MS-Windows(TM) users without Admin rights in mind.

Of course, some power tools are not available. Especially Cljs-REPL or integrant/mount. As powerful as those concepts are, they first need to be mastered. And some of the lack is made up by Babashka's brisk start up time.

One valid objection to bb-web is: one does not need client-side scripting for small web-apps, server side rendering is sufficient. I can only respond that state management on client side is more intuitive to some of us. Moreover, a big advantage of Clojure over Python, Ruby, PHP, Erlang is in Clojurescript, and bb-web is a door leading there.


## Advanced: add Javascript libraries for use with Babashka

You need to install [Clojure](https://www.clojure.org) and [Shadow-cljs](http://shadow-cljs.org) to do this advanced step.

Only one Clojurescript file is behind the scenes of bb-web: ``js/src/bb_web/app.cljs``. It is 50 lines and compiles to the 1MB Javascript file `js/bb_web/bb_web.js` (which is pre-compiled in the repository). 

As you can see in the ``:require`` section of `app.cljs`, two libraries are made available for use with Babashka: Reagent and Ajax.

You can add additional libraries  to the `:require` section and add new functions to the ``:bindings`` map.

To compile, make `bb_web` the current directory and type:
    
    cd js
    echo
    shadow-cljs release bbjs
    cd ..
    bb server.clj

Of course, additional libraries will increase the initial 1MB size. 

If you are compiling for the first time, instead of the (effectless) `echo` command above, type the following two lines:

    npm init -y
    npm install shadow-cljs

If you want the full Shadow-cljs experience while editing, instead of `echo`, type `shadow-cljs watch cljs` and open `http://localhost:8081` in your browser. You will not see any buttons as the Babashka scripts are not loaded. Only the familiar bottom message text is there, maybe change it in `app.cljs`, save and watch Shadows' hot reloading magic happen immediately.

## Related projects

[Lightmod](https://sekao.net/lightmod/) is a long existing full stack Clojure environment with an editor and REPL included. It also does not need Java installation.

## Footnotes

<a name="myfootnote1">1</a>: Opening a command prompt on Mac is via Applications->Utilities->Terminal, on Windows via Start->Run->cmd. Make `bb-web` the current directory using the `cd` command before typing the `bb server.clj` command.
