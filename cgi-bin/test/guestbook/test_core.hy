(import [guestbook.core :as core]
        [guestbook.frame :as f])

(defn assert-equal [x y]
  (assert (= x y)))

(defn wrapper-read [w length]
 (.seek w 0 0)
 (f.decode-bytes (-> w (. buffer) (.read length))))

(defn test_main_1 []
 (setv content "[\"^ \",\"~:name\",\"o\",\"~:message\",\"m\"]")
 (setv read-wrapper (f.text-io-bytes-wrapper content))
 (setv out-wrapper (f.text-io-bytes-wrapper ""))

 (setv header "Content-Type: application/transit+json\n\n")
 (setv h1 "[\"^ \",\"~:messages\",[" )
 (setv h2 "]]")
 (setv out (+ header h1 content h2))

 (f.update_app_state {:f-open-read (fn [] read-wrapper)
                      :environ {"QUERY_STRING"  "route=messages"}
                      :stdout out-wrapper})

 (core.write_transit)
 (.seek out-wrapper 0 0)
 (assert-equal out (wrapper-read out-wrapper (len out))))

(defn sub_main [content fileout sout]
 (setv read-wrapper (f.text-io-bytes-wrapper content))
 (setv write-wrapper (f.my-text-io-bytes-wrapper ""))
 (setv sout-wrapper (f.text-io-bytes-wrapper ""))
 (setv timest "2021-03-30 13:00:00")
 (f.update_app_state {:environ {"QUERY_STRING" "route=message"
                                "CONTENT_LENGTH" (str (len content))}
                      :stdin read-wrapper
                      :stdout sout-wrapper
                      :f-open-append (fn [] write-wrapper)
                      :f-timestamp (fn[] timest)})
 (core.main)
 (assert-equal fileout (wrapper-read write-wrapper 999))
 (assert-equal sout (wrapper-read sout-wrapper 999))
)

(defn test_main_2 []
 (sub_main
   "[\"^ \",\"~:name\",\"o\",\"~:message\",\"m\"]"
   "[\"^ \",\"~:name\",\"o\",\"~:message\",\"m\",\"~:timestamp\",\"2021-03-30 13:00:00\"]\n"
   "Content-Type: application/transit+json\n\n[\"^ \",\"~:result\",null]"))

(defn test_main_3 []
 (sub_main
   "[\"^ \",\"~:name\",\"o\",\"~:message\",\"(+ 3 4)\"]"
   "[\"^ \",\"~:name\",\"o\",\"~:message\",\"(+ 3 4)\",\"~:timestamp\",\"2021-03-30 13:00:00\"]\n"
   "Content-Type: application/transit+json\n\n[\"^ \",\"~:result\",7]"))
