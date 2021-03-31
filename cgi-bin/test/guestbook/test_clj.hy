(import [guestbook.clj [clj-assoc clj-mapv]])

(defn assert-equal [x y]
  (assert (= x y)))

(defn test-clj-assoc []
 (setv b :b)
 (assert-equal (clj-assoc {:a 1} b 2) {:a 1 :b 2}))

(defn test_clj-mapv []
 (assert-equal (clj-mapv inc [1 2 3]) [2 3 4]))
