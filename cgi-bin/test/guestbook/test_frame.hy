(import [guestbook.frame :as f])

(defn assert-equal [x y]
  (assert (= x y)))

(defn read-wrapper [w length]
 (.seek w 0 0)
 (f.decode-bytes (-> w (. buffer) (.read length))))

(f.update_app_state {:f-open-append (fn [] (f.text-io-bytes-wrapper ""))})

(defn test_sprint []
 (setv out-wrapper (f.open-append))
 (f.update_app_state {:stdout out-wrapper})
 (setv text "hello-out")
 (f.sprint text)
 (assert-equal text (read-wrapper out-wrapper (len text))))

(defn test_eprint []
 (setv err-wrapper (f.open-append))
 (f.update_app_state {:stderr err-wrapper})
 (setv text "hello-err")
 (f.eprint text)
 (assert-equal text (read-wrapper err-wrapper (len text))))

(defn test_write-log []
 (setv log-wrapper (f.text-io-bytes-wrapper ""))
 (f.update_app_state {:f-open-log (fn [] log-wrapper)})
 (setv text "hello-log")
 (f.write-log text)
 (assert-equal 1 1))
