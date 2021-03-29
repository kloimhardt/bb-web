(import sys os)

(setv message-filename "examples/n.txt")
(setv log-filename "examples/log.txt")

(setv app-state {"stdin" sys.stdin
                 "stdout" sys.stdout
                 "stderr" sys.stderr
                 "f-open-read" (fn[] (open message-filename "r"))
                 "f-open-append" (fn[] (open message-filename "a"))
                 "f-open-log" (fn[] (open log-filename "a"))
                 "environ" os.environ})

(defn sprint [x]
 (print x :file (. app-state ["stdout"])))

(defn eprint [x]
 (print x :file (. app-state ["stderr"])))

(defn init-attrs []
 (global f-open-read)
 (global f-open-append)
 (global stdin)
 (global stdout)
 (global f-open-log)
 (setv f-open-read (. app-state ["f-open-read"]))
 (setv f-open-append (. app-state ["f-open-append"]))
 (setv stdin (. app-state ["stdin"]))
 (setv stdout (. app-state ["stdout"]))
 (setv f-open-log (. app-state ["f-open-log"])))

(init-attrs)

(defn update-app-state [m]
 (global app-state)
 (.update app-state m)
 (init-attrs))

(defn write-log [x]
 (with [f (f-open-log)]
   (.write f x)
   (.write f "\n")))

(comment
(. app-state ["environ"] ["SHELL"])
(update-app-state {"environ" {"SHELL" "hi"}})
 (init)
 (dentity open-append)
 (sprint "hu")
 (eprint "ha")
 (state-get "messages-filename")
)
