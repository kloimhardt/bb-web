(import
 sys
 os
 [transit.writer [Writer]]
 [transit.reader [Reader]]
 [transit.transit_types [Keyword :as TransitKeyword]]
 [io [BytesIO StringIO]])

(defn eprint [x]
 (print x :file sys.stderr)
 x)

(defn make-dict [a b] {a b})

(defn assoc-level1 [d k v]
 (setv cp (dict (.items d)))
 (assoc cp k v)
 cp)

(setv filename "examples/n.txt")

(comment
 ;; for the repl
 (setv filename "../../examples/n.txt")
 (setv d1 {:b {:a 3}})
 (setv b (.copy a))
 (setv b (assoc b :c 5))
 (print a b d2)

 (setv u {:b {:a 3}})
 (dict (.items u))
 (print u (assoc-level1 u (TransitKeyword "e") 8))
)

(defn read_transit []
 (setv data
  (sys.stdin.buffer.read (int (. os environ ["CONTENT_LENGTH"]))))
 (setv vals
  (-> (Reader "json")
      (.read (BytesIO data))
      (assoc-level1 (TransitKeyword "timestamp") "time")))

 (eprint vals)
 (with [f  (open filename "a")]
   (.write f (.decode data "UTF-8"))
   ;;to read data: (-> (Reader "json") (.read (ByteIO edn-str)))
   (.write f "\n"))
 (print "Content-Type: text/html")
 (print)
 (print (get vals (TransitKeyword "message")))) ;;this print has no effect in cljs

(defn to-pdn [edn-str]
 (-> (Reader "json") (.read (StringIO edn-str))))

(defn write-transit []
 (print "Content-Type: application/transit+json")
 (print)
 (->> (with [f (open filename "r")] (.readlines f))
      (map (fn[t-str] (to-pdn (.rstrip t-str "\n"))))
      list
      (make-dict (TransitKeyword "messages"))
      (.write (Writer sys.stdout "json"))))

(defn main []
 (eprint "in Hy main")
 (setv qs (. os environ ["QUERY_STRING"]))
 (cond
  [(= qs "route=messages") (write-transit)]
  [(= qs "route=message") (read-transit)]))
