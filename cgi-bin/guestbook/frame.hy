(import
 [sys [stdin stdout stderr]]
 [os [environ]]
 [io [TextIOWrapper BytesIO]])

(setv message-filename "examples/n.txt")
(setv log-filename "examples/log.txt")

(setv app-state {:stdin stdin
                   :stdout stdout
                   :stderr stderr
                   :f-open-read (fn[] (open message-filename "r"))
                   :f-open-append (fn[] (open message-filename "a"))
                   :f-open-log (fn[] (open log-filename "a"))
                   :environ environ})

(defn sprint [x]
 (print x :file (. app-state [:stdout])))

(defn eprint [x]
 (print x :file (. app-state [:stderr])))

(defn open-read []
 ((. app-state [:f-open-read])))

(defn open-append []
 ((. app-state [:f-open-append])))

(defn update-app-state [m]
 (global app-state)
 (.update app-state m))

(defn write-log [x]
 (with [f ((. app-state [:f-open-log]))]
   (.write f x)
   (.write f "\n")))

(defn text-io-bytes-wrapper [text]
 (doto (TextIOWrapper (BytesIO) :encoding "utf-8")
  (.write text)
  (.seek 0 0)))

(defn decode-bytes [btext]
 (.decode btext "utf-8"))
