# bb-web

Scripting small web apps in Clojure without installing it. A set of examples is provided, showing increasingly advanced features.

## Getting Started

The following demo, a single page app, is written in Clojure. You can change and run its code without having Java or Clojurescript installed. The only prerequisite is Babashka, a single executable file.

Download [Babashka with httpkit](https://github.com/borkdude/babashka/releases/tag/v0.2.1), a file with the two letter name `bb`.

Then, download this `bb-web` repository via Github's `Code` button above, thereby creating a directory called `bb-web` or similar. Copy the `bb` executable into that directory and double click `bb_web_demo.bat`. Non MS-Windows(TM) users open a command prompt<sup>[1](#myfootnote1)</sup> and type:

    bb server.clj

 If you see the `Could not find: org.httpkit.server` error, by accident you are using a Babashka version that does not support http-kit.

If everything works as expected, your web-browser will open and show some buttons. Try them out.

To code your own ideas, edit the file `client.cljs`. Maybe change the text that is displayed on top of the web-page. Press the browser reload button.

In a first step, it is not needed to understand the back-end part of a web-app to create a nice Web-GUI. But of course you can also edit the file `server.clj`. Maybe change the server greeting text. To see the effect, stop the server pressing `Ctrl+C` and then restart anew as explained above.

## Further examples

### Guestbook 1
The Clojurescript code in ``guestbook_1.cljs`` is based on the guestbook-reagent example of the [Luminus book](https://pragprog.com/titles/dswdcloj3/web-development-with-clojure-third-edition/)<sup>[2](#myfootnote2)</sup>. Only small changes were needed to accommodate for Babashka and not the JVM operating as the server back-end. Itself being still very bare bones, will see improvements in the next examples.

Start by double-clicking on `bb_web_guestbook.bat` or typing

    bb examples/guestbook_1.clj examples/guestbook_1.cljs

### yogthos/graal-web-app-example

Inspired by [this](https://github.com/yogthos/graal-web-app-example) repository, the example needs a Babashka version with [Reitit](https://github.com/metosin/reitit) and [Ring](https://github.com/ring-clojure/ring) included. It would not be in existence without the invaluable help and support of its creator Michiel Borkent. A Windows binary is provided:

https://ci.appveyor.com/api/buildjobs/4swogfjqtuwm5r6j/artifacts/babashka-0.2.1-SNAPSHOT-windows-amd64.zip

Building binaries yourself (e.g. for Mac or Linux) is an advanced issue, it can be done from this [Babashka fork](https://github.com/kloimhardt/babashka)<sup>[3](#myfootnote3)</sup>. 


Start by double-clicking on `yogthos_graal_web_app_example.bat` or typing
```
bb -cp examples -m yogthos-graal-web-app-example
```

### Guestbook 2
The back-end is more sophisticated compared to Guestbook 1. Following and copying Luminus, it includes html templating with [Selmer](https://github.com/yogthos/Selmer), Ring's anti forgery protection, data encoding using [Muuntaja](https://github.com/metosin/muuntaja) and decent http-request error handling using Reitit. Note that the front-end code is still the same `guestbook_1.cljs`.

Start by double-clicking on `bb_web_guestbook_2.bat` or typing

```
bb examples/guestbook_2.clj examples/guestbook_1.cljs
```

## Rationale of bb-web
It offers a low entry bar to Web-development. There is no involved installation process. I especially have MS-Windows users without Admin rights in mind. While for developing Clojure in Windows it is best to use its Subsystem for Linux (WSL), installing WSL is unacceptable for anyone wanting to try out Clojurescript on a weekend, being it a beginner or seasoned F# developer. 

Installing JVM+Clojure on native MS-Windows is not endoresed here. Because of sparse documentation and community, it is for strong souls only. [Lightmod](https://sekao.net/lightmod/) might be an option to be mentioned here.

Babashka (or rather SCI) displays nice error messages. They are more readable than, say, those of self hosted Clojurescript.

It shows some of the good Clojure stuff: same language on the client and the server, Hiccup syntax, Reagent's clean client state management, even a glimpse of hot reloading (include ``(swap! state assoc :hot-reload true)`` in client.cljs to get a respective button).


Of course, some power tools are not available. Especially Cljs-REPL or integrant/mount. As powerful as those concepts are, they first need to be mastered. And some of the lack is made up by Babashka's brisk start up time.

One valid objection to bb-web is: one does not need client-side scripting for small web-apps, server side rendering is sufficient. I can only respond that state management on client side is more intuitive to some of us. Moreover, a big advantage of Clojure over Python, Ruby, PHP, Erlang is in Clojurescript, and bb-web is a door leading there.

## Related projects

[Lightmod](https://sekao.net/lightmod/) is a Clojure development environment which does not need Java installation.


## Advanced stuff (Clojure experience required)

### Expose arbitrary Clojurescript libraries to SCI for subsequent use in front end scripting.

You need to install and use [Shadow-cljs](http://shadow-cljs.org) (and thus Clojure on the JVM) for this advanced step. If you can do this, you do not need `bb-web` anymore as you already mastered Clojurescript. But maybe you want to give an enhanced `bb-web` to others.

Only one Clojurescript file is behind the scenes of bb-web: ``js/src/bb_web/app.cljs``. It is 50 lines and the Clojurescript compiler of Shadow-cljs compiles it to the 1MB Javascript file `js/bb_web/bb_web.js`. 

As you can see in the ``:require`` section of `app.cljs`, two libraries are made available for use in `bb-web` front-end scripting: Reagent and Ajax.

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

If you want the full Shadow-cljs experience while editing, instead of `echo`, type `shadow-cljs watch cljs` and open `http://localhost:8081` in your browser. However, you will not see any buttons as the Babashka scripts are not loaded.

### Running examples with a local Babashka fork

If you do not want to natively compile a local Babashka and have Clojure installed, type:
```
clojure -A:luminus_bb_subset -cp examples -m yogthos-graal-web-app-example

```

You need to edit the `deps.edn` file to point to the right place of your local babashka fork.

## Footnotes

<a name="myfootnote1">1</a>: Opening a command prompt on Mac is via Applications->Utilities->Terminal, on Windows via Start->Run->cmd. Make `bb-web` the current directory using the `cd` command before typing the `bb server.clj` command.

<a name="myfootnote2">2</a>: I have no affiliations with Luminus. But I think the book-format AND -market is still the best way to advance new technology.

<a name="myfootnote3">3</a>: You need to have git installed. Checkout the `guestbook2` branch. Follow the [build instructions](https://github.com/borkdude/babashka/blob/master/doc/build.md). Make sure to have the `BABASHKA_FEATURE_RING` and `BABASHKA_FEATURE_REITIT` feature flags set to `true`. Also set `BABASHKA_FEATURE_SELMER` to `true`, to be ready for following examples.
