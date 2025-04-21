(ns persistent-queue)

(deftype Queue [cnt front rear]
  clojure.lang.Seqable
  (seq [_] (seq front))

  ;; peek
  clojure.lang.IPersistentStack
  (peek [_] (first front))
  (pop [this]
    (let [front* (next front)
          rear* rear]
      (if (nil? front*)
        (Queue.
         (dec cnt)
         (seq rear)
         [])
        (Queue.
         (dec cnt)
         front*
         rear))))

  clojure.lang.Counted
  (count [_] cnt)

  clojure.lang.IPersistentCollection
  (cons [_ o]
    (if-not (seq front)
      (Queue.
       (inc cnt)
       (list o)
       rear)
      (Queue.
       (inc cnt)
       front
       (if (seq rear)
         (conj rear o)
         (vector o)))))

  (empty [_] nil)
  (equiv [_ o] false)


  )


(defmethod print-method Queue [v w]
  (.write w (str "#queue \"" (str (.front v) (.rear v)) "\"")))

(defn queue []
  (Queue. 0 '() []))

(-> (queue)
    (conj 1 2 3 4)
    pop
    pop
    (conj 1 2 3 4))

(comment
  (defn supers-lineage [class]
    (reduce (fn [acc kls]
              (assoc acc kls (supers-lineage kls)))
            {}
            (bases class)))

  ;; find nested super classes of PersistentVector
  (supers-lineage (type []))

  (isa? (type []) clojure.lang.Sequential)

  (seq? [])

  ,)
