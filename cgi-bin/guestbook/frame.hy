(import
 [sys [stdin stdout stderr]]
 [os [environ]])

(setv message-filename "examples/n.txt")
(setv log-filename "examples/log.txt")

(setv f/app-state {:stdin stdin
                   :stdout stdout
                   :stderr stderr
                   :f-open-read (fn[] (open message-filename "r"))
                   :f-open-append (fn[] (open message-filename "a"))
                   :f-open-log (fn[] (open log-filename "a"))
                   :environ environ})

(defn f/print [x]
 (print x :file (. f/app-state [:stdout])))

(defn f/eprint [x]
 (print x :file (. f/app-state [:stderr])))

(defn f/open-read []
 ((. f/app-state [:f-open-read])))

(defn f/open-append []
 ((. f/app-state [:f-open-append])))

(defn f/update-app-state [m]
 (global f/app-state)
 (.update f/app-state m))

(defn f/write-log [x]
 (with [f ((. f/app-state [:f-open-log]))]
   (.write f x)
   (.write f "\n")))
