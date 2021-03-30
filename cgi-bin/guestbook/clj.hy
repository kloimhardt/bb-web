(defn clj/assoc [d k v]
 (setv cp (dict (.items d)))
 (assoc cp k v)
 cp)

(defn clj/mapv [f coll]
 (list (map f coll)))
