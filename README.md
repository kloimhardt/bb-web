# bb-web

Scripting React-ive web apps in Clojure without installing it. 

If you know the meaning of the sentence "I moved to Chlorine from Proto-Repl", stop reading. Otherwise, go on and take a look at Clojure and decide whether it is for you.

 The in-browser UI is interpreted Clojurescript code that you can change and run without having the Cljs-compiler installed.
The Clojure backend code is processed by a single executable file, Java is not necessary. Alternatively, there are Phel/PHP and Hy/Python backends.

Examples go from "Hello World!" to backend features like anti forgery protection, MS-Windows being first class.

## Getting Started

Click on [Guestbook online](https://kloimhardt.github.io/guestbook_0.html) for a first idea (use Chrome browser for best performance).

Then, using the green `Code` button you see on top of this page, download the zip file of this `bb-web` repository. Unzip it, thereby creating some directory called `bb-web-master` or similar. 

In your file explorer, double click on `first.html`. A button called "Count up" should appear. Press it and see the Small Clojure Interpreter ([SCI](https://github.com/borkdude/sci)) in action in the browser.

The file `first.html` has only 12 lines of code:

```
<script src="js/bb_web/bb_web.js"></script>
<div id="cljs-app">
 (defn component []
   [:div
    [:button
     {:on-click (fn [_] (swap! bb-web/state update :counter inc))}
     "Count up"]
     (str " " (or (:counter (deref bb-web/state)) 0))])

  [component]
</div>
<script>bb_web.app.run("cljs-app")</script>
```

Open it in some text editor and change the button text to "Count up by one". Save and press the browser reload button. One other option is to copy-paste all code from the above online Guestbook and run it locally (although no nice styling - yet!).

If Clojure is new to you, run the examples in [clj-tiles](https://kloimhardt.github.io/cljtiles.html) and try the [Koans](http://clojurescriptkoans.com) (be patient with the white screen for some seconds). After that you will know how to use `deref` (amongst other things).

Note that the variable `bb-web/state` you see in the code is special. It is automatically updated on the screen wherever its content is displayed (by means of  `deref`). Within bb-web, you cannot create such useful beasts on your own. However, you can do that after switching to the more feature rich [Scittle](https://borkdude.github.io/scittle/), a step which is not within scope here but nevertheless recommended after grasping the basics.

## Further examples

### Using Babashka as back-end

 Download [Babashka](https://github.com/borkdude/babashka/releases), a single executable file with the two letter name `bb`. Copy it into the `bb-web-master` directory created before. Double click `start.bat` and select option 1, or open the console window on your own<sup>[1](#myfootnote1)</sup> and type:

    bb examples/start.clj

Your web-browser will open and show some buttons. Try them out.

To code your own ideas, edit the Clojurescript file `examples/start.cljs` (not the Clojure file `start.clj`, notice the last "s" for "script"). Maybe change the text that is displayed on top of the web-page. Press reload in your browser.

In this first step, playing with the Web-GUI, it is not needed to understand the server back-end part. But of course you can also edit the Clojure file `start.clj`. Maybe change the server greeting text. To see the effect, stop the server by pressing `Ctrl+C` in the command prompt window and then restart anew as explained above.

### Hot reload

Start by double-clicking on `start.bat` (select option 2) or type

```
bb examples/hot_reload.clj examples/hot_reload.cljs 
```

Increase the counter. Then edit some text in `examples/hot_reload.cljs` and save. Instead of pressing the browser reload button, press `hot reload` and notice that the counter preserves its value while the text on screen reflects your changes.

If the handling of pranentheses while editing feels cumbersome, maybe the later introduced Parinfer is for you.

### Luminus Guestbook minimal back-end

The Clojurescript code in ``guestbook_1.cljs`` is based on the guestbook-reagent example of the [Luminus book](https://pragprog.com/titles/dswdcloj3/web-development-with-clojure-third-edition/)<sup>[2](#myfootnote2)</sup> (example code in [this zip file](http://media.pragprog.com/titles/dswdcloj3/code/dswdcloj3-code.zip)). Only small changes were needed to accommodate for Babashka and not the JVM operating as the server back-end. Itself being still very bare bones, will see improvements in the next examples.

Start by double-clicking on `start.bat` or type

    bb examples/guestbook_1.clj examples/guestbook_1.cljs

To see the nice css styling, you need WiFi connection, a requirement that will be dropped in a later example.

### Lumius Guestbook rich back-end
The back-end is more sophisticated compared to `guestbook_1.clj`.

This example needs a Babashka version with [Reitit](https://github.com/metosin/reitit) and [Ring](https://github.com/ring-clojure/ring) included. It would not be in existence without the invaluable help and support of its creator Michiel Borkent. Building this binary is an advanced issue, some guidance is given below. A Windows binary is provided:

https://github.com/kloimhardt/babashka-web/releases

Note the new name of the executable: `bb-web`.

Following and copying Luminus, the example includes html templating with [Selmer](https://github.com/yogthos/Selmer), Ring's anti forgery protection, data encoding using [Muuntaja](https://github.com/metosin/muuntaja) and decent http-request error handling using Reitit. Start by double-clicking on `start.bat` (select option 4) or type

```
bb-web examples/guestbook_2.clj examples/guestbook_1.cljs
```

Note that the front-end code did not change, it is still the above `guestbook_1.cljs`.

### Edit with parinfer-codemirror

Start by double-clicking on `start.bat` or type
```
bb-web -cp examples -m parinfer-codemirror
```
Within the Codemirror editor, the file `examples/hot_reload.cljs` is shown. It is best to start the hot-reload example described above in order to see the effects of editing and saving code. As opposed to the usual editing experience, [Parinfer](http://shaunlebron.github.io/parinfer/demo) takes care of balancing parentheses according to your indentation. Decide whether it is for you<sup>[3](#myfootnote3)</sup>.

This example does not need WiFi connection for its CSS styling.

## Hy/Python backend
[Hy](https://docs.hylang.org/en/stable/) is inspired by Clojure, it compiles to [Python](https://www.python.org). Using Hy as backend language opens up the vast Python ecosystem to ClojureScript whilst still remaining in the Lisp paradigm.

You need to install Python 3.7 (or later) and start a server with:

```
python3 -m http.server --cgi
```
Then, in the adress bar of your browser, type:
```
http://localhost:8000/guestbook_3.html
```
Notice that the ClojureScript code thus called is virtually unchanged compared to `guestbook_1.cljs` which connects to the backends described above, written in Clojure.

The main Hy code is in the file `cgi-bin/guestbook/core.hy`. It allows Hy expressions to be sent to the server, where they are executed. Try typing `(+ 7 (get {:a {:b 3}} :a :b))` into the message field of your guestbook and see what happens (notice that Hy's `get` is more akin to Clojure's `get-in`).

## Phel/PHP backend
[Phel](https://phel-lang.org) is inspired by Clojure, it compiles to [PHP](https://www.php.net). Using Phel as backend language allows ClojureScript to be served on cheap shared hosting whilst still remaining in the Lisp paradigm.

You need to install PHP 7.4 (or later). Then download Phel with:
```
php composer.phar install

```
Start a server with:
```
php -S localhost:8000
```
Then, in the adress bar of your browser, type:
```
http://localhost:8000
```

The Phel code is in the file `examples/guestbook4.phel`, it is very similar to Clojure. The Clojurescript code is in `examples/guestbook_4.cljs`, the only change compared to `guestbook_1.cljs` is that JSON is used to exchange data instead of Transit.

Thanks to [Mario Bašić](https://github.com/mabasic/mariobasic-n7) for opening the code of his personal website built with Phel.

## Rationale of bb-web

It offers a low entry bar to Web-development. There is no involved installation process. I especially have MS-Windows users in mind<sup>[4](#myfootnote4)</sup>.

``bb-web`` shows some of the good Clojure stuff: same language on the client and the server, Hiccup syntax, Reagent's clean client state management, even a glimpse of hot reloading.

Babashka's underlying Small Clojure Interpreter ([SCI](https://github.com/borkdude/sci)) displays nice error messages.

The REPL is not needed because of Babashka's brisk start up time. Here are three reasons why it can make sense to avoid the REPL in a first step: 

1) The REPL distracts from the main hallmark of Clojure: immutable data structures with `swap!` being the exception. True, using the REPL, one can fix a bug in a running instance of a program, indeed an important feature of LISP languages. But this effectiveness of the REPL is based on mutation of code and data, which is exactly the opposite of demonstrating immutability. For this reason, the REPL is confusing for anyone trying to understand why Clojure people talk so much about immutability.

2) The REPL has its own learning curve. The replacing or adding of code in a running program, because of being mutation, needs advanced knowledge and care (using #' or component/integrant/mount is only the peak of the iceberg). One can easily get to a state where what's in memory does not match up to what's on disk. As a result, a perfectly well functioning program can refuse to work after restart. A frustration to be avoided in a first step (but with time becoming THE indispensable tool for LISP programming because the save-compile cycle is skipped resulting in a fast feedback loop).

3) The REPL is in practice not used without a properly configured development environment (i.e. editor). As said in this influential [Video](https://www.youtube.com/watch?v=Qx0-pViyIDU&feature=youtu.be&t=740) on the topic: "you work in your favourite tool", i.e. IntelliJ, Calva, Chlorine or Cider. This last tool is presented [here](https://www.youtube.com/watch?v=NDrpclY54E0), which demonstrates that one has to prepare a few things before using the REPL in its intended way. To be sure, after a certain understanding of the bb-web examples, that preparation should be the second step.

One valid objection to bb-web is: one does not need client-side cljs scripting for small web-apps, server side rendering is sufficient. I can only respond that state management on client side is more intuitive to some of us. Moreover, the simplest example of this repository, the 12-line first.html, is pure ClojureScript - it needs no installation because the browser is everywhere.

## Related projects

[Lightmod](https://sekao.net/lightmod/) is a Clojure development environment which does not need Java installation.


## Advanced topics (Clojure experience required)

The following descriptions are less complete than the previous sections. If you understand them, you do not need `bb-web` yourself. They show you how to give enhanced `bb-web` features to potential new Clojure users.

### Expose arbitrary Clojurescript libraries to the SCI 

Before going this route, it is recommended to have a look at the more sophisticated [Scittle](https://borkdude.github.io/scittle/).

To start hacking `bb-web`, you need to install and use [Shadow-cljs](http://shadow-cljs.org) (and thus Clojure on the JVM). 

Only one Clojurescript file is behind the scenes of bb-web: ``js/src/bb_web/app.cljs``. It is 50 lines and the Clojurescript compiler of Shadow-cljs compiles it to the 1MB Javascript file `js/bb_web/bb_web.js`. 

As you can see in the ``:require`` section of `app.cljs`, two libraries are made available for use in `bb-web` front-end scripting: Reagent and Ajax.

You can add additional libraries  to the `:require` section and add new functions to the ``:bindings`` map.

To compile, in the `js` directory type `shadow-cljs release bbjs`.

If you want the full Shadow-cljs experience while editing, type `shadow-cljs watch cljs` and open `http://localhost:8080` in your browser.

### Running examples with a local Babashka fork

If you do not want to natively compile a local Babashka and have Clojure installed, type:
```
clojure -A:luminus_bb_subset -cp examples -m parinfer-codemirror

```

You need to edit the `deps.edn` file to point to the right place of your local babashka fork. Doing ``rm -rf .cpcache/`` might help. And do not forget to `export BABASHKA_FEATURE_*  = true` environment variables (see below for the actual names).

### Building a Windows executable

Connect [AppVayor](https://www.appveyor.com) to this [Babashka fork](https://github.com/kloimhardt/babashka). For every new Git commit a build is made.

### Building binaries for any platform

Follow the [build instructions](https://github.com/borkdude/babashka/blob/master/doc/build.md). Make sure to have the shell environment variables `BABASHKA_FEATURE_RING`, `BABASHKA_FEATURE_REITIT`, `BABASHKA_FEATURE_SELMER` set to `true` (in bash via the `export` command).


## Footnotes

<a name="myfootnote1">1</a>: Opening a command prompt on Mac is via Applications->Utilities->Terminal, on Windows via Start->Run->cmd. Make `bb-web-master` the current directory using the `cd` command before typing the `bb ...` commands. The console window showing the command prompt is most probably white text on dark background.

<a name="myfootnote2">2</a>: I have no affiliations with Luminus. But I think the book-format AND -market is still the best way to advance new technology.

<a name="myfootnote3">3</a>: I started Clojure using Parinfer. Its ingenious (and only useful) "smart" mode was available early on in the Atom editor. I moved to Spacemacs though because of Cider (no smart mode there so learned Paredit). 

<a name="myfootnote4">4</a>: The topic of using JVM+Clojure on native MS-Windows is not in scope here. [scoop-clojure](https://github.com/littleli/scoop-clojure) might be a good starting option. I develop in Windows using its Subsystem for Linux (WSL) - and yes, also use the JVM.
