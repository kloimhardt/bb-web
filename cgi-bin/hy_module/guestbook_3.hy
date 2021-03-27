(import
 sys
 os
 [transit.writer [Writer]]
 [transit.reader [Reader]]
 [transit.transit_types [Keyword]]
 [io [BytesIO]])

(defn eprint [x]
 (print x :file sys.stderr)
 x)

(defn read_transit []
 (setv daten
  (-> sys.stdin.buffer (.read (int (. os environ ["CONTENT_LENGTH"])))))
 (setv vals (-> (Reader "json") (.read (BytesIO daten))))
 (print "Content-Type: text/html")
 (print)
 (print (get vals (Keyword "message"))))

(defn write-transit []
 (print "Content-Type: application/transit+json")
 (print)
 (-> (Writer sys.stdout "json")
  (.write [(. os environ ["QUERY_STRING"]) "abc" 1234567890])))

(defn main []
 (eprint "in Hy main")
 (setv qs (. os environ ["QUERY_STRING"]))
 (cond
  [(= qs "route=messages") (write-transit)]
  [(= qs "route=message") (read-transit)]))
