(import
 datetime
 [guestbook.clj :as clj]
 [guestbook.frame :as frame]
 [transit.writer [Writer]]
 [transit.reader [Reader]]
 [transit.transit_types [Keyword :as TransitKeyword]]
 [io [BytesIO StringIO]])

(defn bytes-to-pydata [transit-bytes]
 (-> (Reader "json") (.read (BytesIO transit-bytes))))

(defn read_transit []
 (setv clen (. frame app-state ["environ"] ["CONTENT_LENGTH"]))
 (setv stdin-bytes
  (frame.stdin.buffer.read (int clen)))
 (setv time (-> datetime.datetime .now (.strftime "%Y-%m-%d %H:%M:%S")))
 (setv data
  (-> (bytes-to-pydata stdin-bytes)
      (clj.assoc (TransitKeyword "timestamp") time)))
 (.write-log frame clen)
 (.write-log frame (str stdin-bytes))
 (with [f (.f-open-append frame)]
   (.write (Writer f "json") data)
   (.write f "\n"))
 (frame.sprint "Content-Type: text/html")
 (frame.sprint "")
 (frame.sprint "success"))

(defn str-to-pydata [transit-str]
 (-> (Reader "json") (.read (StringIO transit-str))))

(defn write-transit []
 (frame.sprint "Content-Type: application/transit+json")
 (frame.sprint "")
 (->> (with [f (.f-open-read frame)] (.readlines f))
      (clj.mapv (fn[t-str] (str-to-pydata (.rstrip t-str "\n"))))
      (clj.assoc {} (TransitKeyword "messages"))
      (.write (Writer frame.stdout "json"))))

(defn main []
 (frame.eprint "in Hy main")
 (setv qs (. frame app-state ["environ"] ["QUERY_STRING"]))
 (.write-log frame qs)
 (cond
  [(= qs "route=messages") (write-transit)]
  [(= qs "route=message") (read-transit)]))
