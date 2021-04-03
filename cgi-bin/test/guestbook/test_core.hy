(import [guestbook.core :as core]
        [guestbook.frame :as f]
        [transit.transit_types [Keyword :as TransitKeyword]])

(defn assert-equal [x y]
  (assert (= x y)))

(defn wrapper-read [w length]
 (.seek w 0 0)
 (f.decode-bytes (-> w (. buffer) (.read length))))

(defn sub-main-get [content sout]
 (setv read-wrapper (f.text-io-bytes-wrapper content))
 (setv sout-wrapper (f.text-io-bytes-wrapper ""))
 (f.update_app_state {:f-open-read (fn [] read-wrapper)
                      :environ {"QUERY_STRING"  "route=messages"}
                      :stdout sout-wrapper})

 (core.main-handler)
 (assert-equal sout (wrapper-read sout-wrapper (len sout))))

(defn test-main-1 []
 (sub_main_get
  "[\"^ \",\"~:name\",\"o\",\"~:message\",\"m\"]"
  "Content-Type: application/transit+json\n\n[\"^ \",\"~:messages\",[[\"^ \",\"~:name\",\"o\",\"~:message\",\"m\"]]]"))

(defn sub-main-post [content fileout sout]
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
 (core.main-handler)
 (assert-equal fileout (wrapper-read write-wrapper 999))
 (assert-equal sout (wrapper-read sout-wrapper 999)))

(defn test-main-2 []
 (sub_main_post
   "[\"^ \",\"~:name\",\"o\",\"~:message\",\"m\"]"
   "[\"^ \",\"~:name\",\"o\",\"~:message\",\"m\",\"~:timestamp\",\"2021-03-30 13:00:00\"]\n"
   "Content-Type: application/transit+json\n\n[\"^ \",\"~:result\",null]"))

(defn test-main-3 []
 (sub_main_post
   "[\"^ \",\"~:name\",\"o\",\"~:message\",\"(+ 3 4)\"]"
   "[\"^ \",\"~:name\",\"o\",\"~:message\",\"(+ 3 4)\",\"~:timestamp\",\"2021-03-30 13:00:00\"]\n"
   "Content-Type: application/transit+json\n\n[\"^ \",\"~:result\",7]"))

(defn test-replace-hy-keywords []
 (assert-equal (core.replace-hy-keywords {:a {:b {:c 1}}})
               {(TransitKeyword "a")
                {(TransitKeyword "b")
                 {(TransitKeyword "c") 1}}}))

(defn test-main-4 []
   (sub_main_post
     "[\"^ \",\"~:message\",\"(str (do\\n(import [sympy [*]])\\n(setv x (Symbol \\\"x\\\"))\\n(+ x 1)\\n))\",\"~:name\",\"sympyval\"]"
     "[\"^ \",\"~:message\",\"(str (do\\n(import [sympy [*]])\\n(setv x (Symbol \\\"x\\\"))\\n(+ x 1)\\n))\",\"~:name\",\"sympyval\",\"~:timestamp\",\"2021-03-30 13:00:00\"]\n"
     "Content-Type: application/transit+json\n\n[\"^ \",\"~:result\",\"x + 1\"]"))

(comment
;;this works in the message field of the browser
(str
(do
(import [sympy [*]])
(setv x (Symbol "x"))
[(limit (/ (sin x) x) x 0)
(integrate (/ 1 x) x)])))
