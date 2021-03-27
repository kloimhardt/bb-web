(import
 sys
 os
 datetime
 [transit.writer [Writer]]
 [transit.reader [Reader]]
 [transit.transit_types [Keyword :as TransitKeyword]]
 [io [BytesIO StringIO]])

(defn eprint [x]
 (print x :file sys.stderr)
 x)

(defn assoc-level1 [d k v]
 (setv cp (dict (.items d)))
 (assoc cp k v)
 cp)

(setv filename "examples/n.txt")

(comment
 ;; for the repl
 (setv filename "../../examples/n.txt")
 )

(defn bytes-to-pydata [transit-bytes]
 (-> (Reader "json") (.read (BytesIO transit-bytes))))

(defn read_transit []
 (setv stdin-bytes
  (sys.stdin.buffer.read (int (. os environ ["CONTENT_LENGTH"]))))
 (setv time (-> datetime.datetime .now (.strftime "%Y-%m-%d %H:%M:%S")))
 (setv data
  (-> (bytes-to-pydata stdin-bytes)
      (assoc-level1 (TransitKeyword "timestamp") time)))
 (with [f (open filename "a")]
   (.write (Writer f "json") data)
   (.write f "\n"))
 (print "Content-Type: text/html")
 (print)
 (print "success"))

(defn str-to-pydata [transit-str]
 (-> (Reader "json") (.read (StringIO transit-str))))

(defn write-transit []
 (print "Content-Type: application/transit+json")
 (print)
 (->> (with [f (open filename "r")] (.readlines f))
      (map (fn[t-str] (str-to-pydata (.rstrip t-str "\n"))))
      list
      (assoc-level1 {} (TransitKeyword "messages"))
      (.write (Writer sys.stdout "json"))))

(defn main []
 (eprint "in Hy main")
 (setv qs (. os environ ["QUERY_STRING"]))
 (cond
  [(= qs "route=messages") (write-transit)]
  [(= qs "route=message") (read-transit)]))
