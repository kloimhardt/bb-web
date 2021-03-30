(import
 [datetime [datetime :as d/datetime]]
 [guestbook.clj [clj/assoc clj/mapv]]
 [guestbook.frame
  [f/app-state f/write-log f/open-append f/open-read f/print f/eprint]]
 [transit.writer [Writer]]
 [transit.reader [Reader]]
 [transit.transit_types [Keyword :as TransitKeyword]]
 [io [BytesIO StringIO]])

(defn bytes-to-pydata [transit-bytes]
 (-> (Reader "json") (.read (BytesIO transit-bytes))))

(defn read_transit []
 (setv clen (get f/app-state :environ "CONTENT_LENGTH"))
 (setv stdin-bytes
  (-> (get f/app-state :stdin) (. buffer) (.read (int clen))))
 (setv time (-> d/datetime .now (.strftime "%Y-%m-%d %H:%M:%S")))
 (setv data
  (-> (bytes-to-pydata stdin-bytes)
      (clj/assoc (TransitKeyword "timestamp") time)))
 (f/write-log clen)
 (f/write-log (str stdin-bytes))
 (with [f (f/open-append)]
   (.write (Writer f "json") data)
   (.write f "\n"))
 (f/print "Content-Type: text/html")
 (f/print "")
 (f/print "success"))

(defn str-to-pydata [transit-str]
 (-> (Reader "json") (.read (StringIO transit-str))))

(defn write-transit []
 (f/print "Content-Type: application/transit+json")
 (f/print "")
 (->> (with [f (f/open-read)] (.readlines f))
      (clj/mapv (fn[t-str] (str-to-pydata (.rstrip t-str "\n"))))
      (clj/assoc {} (TransitKeyword "messages"))
      (.write (Writer (get f/app-state :stdout) "json"))))

(defn core/main []
 (f/eprint "in Hy main")
 (setv qs (get f/app-state :environ "QUERY_STRING"))
 (cond
  [(= qs "route=messages") (write-transit)]
  [(= qs "route=message") (read-transit)]))
