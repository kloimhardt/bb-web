# bb-web

Scripting React-ive web apps in Clojure without installing it. 

The in-browser UI is interpreted Clojurescript code that you can change and run without having the Cljs-compiler or Java installed.

Examples go from "Hello World!" to back-end features like anti forgery protection, MS-Windows being first class.

## Getting Started

Download the zip file of this `bb-web` repository via Github's `Code` button above. Unzip it, thereby creating some directory called `bb-web-master`. 

In your file explorer, double click on `first.html`. A button called "Count up" should appear. Press it and see the Small Clojure Interpreter ([SCI](https://github.com/borkdude/sci)) in action in the browser.

The file `first.html` has only 10 lines of code:

```
<div id="cljs-app">
 (fn []
   [:div
    [:button
     {:on-click (fn [_] (swap! bb-web/state update :counter inc))}
     "Count up"]
     (str " " (or (:counter (deref bb-web/state)) 0))])
</div>

<script src="js/bb_web/bb_web.js"></script>
```

Open it in some text editor and change the button text to "Count up by one". Save and press the browser reload button. 

If Clojure is new to you, run the examples in [CLJ-BLOCKS](https://kloimhardt.github.io/clj_blocks.html) and try the [Koans](http://clojurescriptkoans.com). After that you will know how to use `deref` (amongst other things).

Note that the variable `bb-web/state` is special. It is automatically updated on the screen when its content is accessed by `deref`. Within bb-web, you cannot create such useful beasts on your own, but arguably having only one of those is exactly right for even the most sophisticated purposes.

It is fair to say that understanding the 6 lines of Cloure code in the above example means understanding Clojure. But in any case, also run the next example.

## Further examples

### Guestbook 0

Double click on `guestbook_0.html`. To see the nice css styling, you need WiFi connection, a requirement that will be dropped in a later example.

### Using Babashka as back-end

 Download [Babashka](https://github.com/borkdude/babashka/releases/tag/v0.2.1), a single executable file with the two letter name `bb`. Copy it into the `bb-web-master` directory created before. Double click `bb_web_demo.bat`, or open a command prompt<sup>[1](#myfootnote1)</sup> and type:

    bb server.clj

 If you see the `Could not find: org.httpkit.server` error, by accident you are using a Babashka version that does not support http-kit.

If everything works as expected, your web-browser will open and show some buttons. Try them out.

To code your own ideas, edit the file `client.cljs`. Maybe change the text that is displayed on top of the web-page. Press reload in your browser.

You can even start with a different client, like so:
```
bb server.clj examples/client_hot_reload.cljs
```
Do not be surprised that the new hot-reload button does not work yet, it will in the example below.

In this first step, it is not needed to understand the server back-end part to create a nice Web-GUI. But of course you can also edit the file `server.clj`. Maybe change the server greeting text. To see the effect, stop the server pressing `Ctrl+C` and then restart anew as explained above.

### Hot reload

Start by double-clicking on `bb_web_hot_reload.bat` or type

```
bb examples/server_hot_reload.clj examples/client_hot_reload.cljs 
```

Increase the counter. Then edit some text in `examples/client_hot_reload.cljs` and save. Instead of pressing the browser reload button, press `hot reload` and notice that the counter preserves its value while the text changes as expected.

If the handling of pranentheses while editing feels cumbersome, maybe the later introduced Parinfer is for you.

### Guestbook 1

The Clojurescript code in ``guestbook_1.cljs`` is based on the guestbook-reagent example of the [Luminus book](https://pragprog.com/titles/dswdcloj3/web-development-with-clojure-third-edition/)<sup>[2](#myfootnote2)</sup>. Only small changes were needed to accommodate for Babashka and not the JVM operating as the server back-end. Itself being still very bare bones, will see improvements in the next examples.

Start by double-clicking on `bb_web_guestbook_1.bat` or type

    bb examples/guestbook_1.clj examples/guestbook_1.cljs

### Edit with parinfer-codemirror

This example needs a Babashka version with [Reitit](https://github.com/metosin/reitit) and [Ring](https://github.com/ring-clojure/ring) included. It would not be in existence without the invaluable help and support of its creator Michiel Borkent. Building this binary is an advanced issue, some guidance is given below. A Windows binary is provided:

https://github.com/kloimhardt/babashka/releases/tag/v0.2.3

Start by double-clicking on `bb_web_parinfer_codemirror.bat` or type (note the new name of the executable: `bb-web`)
```
bb-web -cp examples -m parinfer-codemirror
```
It shows the file `examples/client_hot_reload.cljs` in the Codemirror editor. As opposed to the usual editing experience, [Parinfer](http://shaunlebron.github.io/parinfer/demo) takes care of balancing parentheses according to your indentation. Indeed this example is a way to try out Paredit and decide whether it is for you<sup>[3](#myfootnote3)</sup>.

It is meant that the above hot-relaod example is started along with this one.

This example does not need WiFi connection for its CSS styling.

### Guestbook 2
The back-end is more sophisticated compared to Guestbook 1. Following and copying Luminus, it includes html templating with [Selmer](https://github.com/yogthos/Selmer), Ring's anti forgery protection, data encoding using [Muuntaja](https://github.com/metosin/muuntaja) and decent http-request error handling using Reitit. Note that the front-end code is still the same `guestbook_1.cljs`.

Start by double-clicking on `bb_web_guestbook_2.bat` or typing

```
bb-web examples/guestbook_2.clj examples/guestbook_1.cljs
```

## Rationale of bb-web

It offers a low entry bar to Web-development. There is no involved installation process. I especially have MS-Windows users in mind<sup>[4](#myfootnote4)</sup>.

``bb-web`` shows some of the good Clojure stuff: same language on the client and the server, Hiccup syntax, Reagent's clean client state management, even a glimpse of hot reloading.


Babashka's underlying Small Clojure Interpreter ([SCI](https://github.com/borkdude/sci)) displays nice error messages. They are more readable than, say, those of self hosted Clojurescript.

Of course, some power tools are not available. Especially Cljs-REPL or integrant/mount. As powerful as those concepts are, they first need to be mastered. And some of the lack is made up by Babashka's brisk start up time.

Additional notes on why one should in a first step avoid the REPL: 
1) Without a properly configured development environment (i.e. editor), the REPL is a pain to use. As said in this influential [Video](https://www.youtube.com/watch?v=Qx0-pViyIDU&feature=youtu.be&t=740) on the topic: "you work in your favourite tool", i.e. Cider, IntelliJ, Calva, Clorine. And yet, many Clojure books begin with starting a REPL in the terminal window (for the understandable reason that endorsing a particular IDE is not in the intention of the writer).
2) A beginner needs to understand first and foremost, that Clojure is all about immutable data structures and using the `swap!` function only in special cases. The REPL, on the other hand, is all about mutation, namely about mutating code in a running program. I think that leads to confusion.

One valid objection to bb-web is: one does not need client-side scripting for small web-apps, server side rendering is sufficient. I can only respond that state management on client side is more intuitive to some of us. Moreover, a big advantage of Clojure over Python, Ruby, PHP, Erlang is in Clojurescript, and bb-web is a door leading there.

## Related projects

[Lightmod](https://sekao.net/lightmod/) is a Clojure development environment which does not need Java installation.


## Advanced topics (Clojure experience required)

The following descriptions are less complete than the previous sections. If you understand them, you do not need `bb-web` yourself. They show you how to give enhanced `bb-web` features to potential new Clojure users.

### Expose arbitrary Clojurescript libraries to the SCI 

You need to install and use [Shadow-cljs](http://shadow-cljs.org) (and thus Clojure on the JVM). 

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
clojure -A:luminus_bb_subset -cp examples -m parinfer-codemirror

```

You need to edit the `deps.edn` file to point to the right place of your local babashka fork. Doing ``rm -rf .cpcache/`` might help. And do not forget to `export BABASHKA_FEATURE_*  = true` environment variables (see below for the actual names).

### Building a Windows executable

You need to have git installed. Checkout the `guestbook2` branch of this [Babashka fork](https://github.com/kloimhardt/babashka). Connect [AppVayor](https://www.appveyor.com) to this fork. For every new Git commit a build is made.

### Building binaries for any platform

Follow the [build instructions](https://github.com/borkdude/babashka/blob/master/doc/build.md). Make sure to have the shell environment variables `BABASHKA_FEATURE_RING`, `BABASHKA_FEATURE_REITIT`, `BABASHKA_FEATURE_SELMER` set to `true` (in bash via the `export` command).


## Footnotes

<a name="myfootnote1">1</a>: Opening a command prompt on Mac is via Applications->Utilities->Terminal, on Windows via Start->Run->cmd. Make `bb-web` the current directory using the `cd` command before typing the `bb server.clj` command.

<a name="myfootnote2">2</a>: I have no affiliations with Luminus. But I think the book-format AND -market is still the best way to advance new technology.

<a name="myfootnote3">3</a>: I started Clojure using Parinfer. Its ingenious (and only useful) "smart" mode was available early on in the Atom editor. I moved to Spacemacs though because of Cider (no smart mode there so learned Paredit). 

<a name="myfootnote4">4</a>: Developing lager Clojure projects in Windows is best done using its Subsystem for Linux (WSL). The topic of using JVM+Clojure on native MS-Windows is not in scope here. In a way, `bb-web` is there to avoid this particular rabbit hole.
