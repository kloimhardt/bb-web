(import
 [guestbook.clj [clj-assoc clj-mapv]]
 [guestbook.frame
  [app-state write-log open-append open-read sprint eprint timestamp]]
 [guestbook.pytools [hyeval]]
 [transit.writer [Writer]]
 [transit.reader [Reader]]
 [transit.transit_types [Keyword :as TransitKeyword]]
 [io [BytesIO StringIO]])

(defn bytes-to-pydata [transit-bytes]
 (-> (Reader "json") (.read (BytesIO transit-bytes))))

(defn read_transit []
 (setv clen (get app-state :environ "CONTENT_LENGTH"))
 (setv stdin-bytes
  (-> (get app-state :stdin) (. buffer) (.read (int clen))))
 (setv data
  (-> (bytes-to-pydata stdin-bytes)
      (clj-assoc (TransitKeyword "timestamp") (timestamp))))
 (setv msge (get data (TransitKeyword "message")))
 (with [f (open-append)]
   (.write (Writer f "json") data)
   (.write f "\n"))
 (sprint "Content-Type: application/transit+json")
 (sprint "")
 (->> (if (= (first msge) "(") (hyeval msge) None)
      (clj-assoc {} (TransitKeyword "result"))
      (.write (Writer (get app-state :stdout) "json"))))

(defn str-to-pydata [transit-str]
 (-> (Reader "json") (.read (StringIO transit-str))))

(defn write-transit []
 (sprint "Content-Type: application/transit+json")
 (sprint "")
 (->> (with [f (open-read)] (.readlines f))
      (clj-mapv (fn[t-str] (str-to-pydata (.rstrip t-str "\n"))))
      (clj-assoc {} (TransitKeyword "messages"))
      (.write (Writer (get app-state :stdout) "json"))))

(defn main []
 (eprint "in Hy main")
 (setv qs (get app-state :environ "QUERY_STRING"))
 (cond
  [(= qs "route=messages") (write-transit)]
  [(= qs "route=message") (read-transit)]))
