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

Another idea is to change `true` to `false` where it appears in the code and find out what it does.

In a first step, it is not needed to understand the back-end part of a web-app to create a nice Web-GUI. But of course you can also edit the file `server.clj`. Maybe change the server greeting text. To see the effect, stop the server pressing `Ctrl+C` and restarting with the familiar `bb server.clj`.

## Further example

### Guestbook
This example is more involved. The file ``guestbook_1.cljs`` is delibarately taken from the guestbook-reagent example of the [Luminus book](https://pragprog.com/titles/dswdcloj3/web-development-with-clojure-third-edition/) <sup>[2](#myfootnote2)</sup>, so there is plenty of explanations if needed. Only small code changes were needed to accommodate for Babashka (and not the JVM) being the server back-end.

Start by double-clicking on `bb_web_guestbook.bat` or typing

    bb examples/guestbook_1.clj examples/guestbook_1.cljs

### yogthos/graal-web-app-example

This example needs a Babashka version with Reitit and Ring included. It would not be in existence without the invaluable help and support of its creator Michiel Borkent. A Windows binary is provided: 

[https://ci.appveyor.com/api/buildjobs/yjlneqm3mh61taex/artifacts/babashka-0.2.1-SNAPSHOT-windows-amd64.zip]
(https://ci.appveyor.com/api/buildjobs/yjlneqm3mh61taex/artifacts/babashka-0.2.1-SNAPSHOT-windows-amd64.zip)

Binaries for Mac and Linux can be buildt from this [Babashka fork](https://github.com/kloimhardt/babashka). Ring and Reitit had to be patched so that the Graal compile size stays at 70M (instead of 105M). Also Ring needed a patch to access the io/resource function of Babashka and not Java's, this is because Graal has a different notion of Classpath than Babashka.
```
bb -cp examples -m yogthos-graal-web-app-example
```
If you have Clojure installed, but do not want to compile, type:
```
clojure -A:luminus_bb_subset -cp examples -m yogthos-graal-web-app-example

```

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

<a name="myfootnote2">2</a>: I have no affiliations with Luminus. But I think the book-format AND -market is still the best way to advance new technology.
