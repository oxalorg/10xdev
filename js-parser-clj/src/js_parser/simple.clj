(ns js-parser.simple
  (:require [clojure.string :as str]))

(def program "1 + 2")

(defn parse-digit []
  (fn [s]
    (let [c (first s)]
      (cond
        (nil? c) []
        (<= (int \0) (int c) (int \9)) [c (rest s)]
        :else []))))

((parse-digit) "12 abc")

(defn *result [v]
  (fn **result [s]
    [v s]))

(defn pzero [s]
  [])

(defn pitem [s]
  (cond
    (not (seq s)) []
    :else [(first s) (rest s)]))

(defn *seq [p1 p2]
  (fn **seq [s]
    (let [[v s*] (p1 s)
          [w s**] (p2 s*)]
      [[v w] s**])))

(defn *bind [p f]
  (fn **bind [s]
    (let [[v s*] (p s)]
      [(f v) (f s*)])))

(defn *sat [pred]
  (*bind
   pitem
   (fn [x]
     (if (pred x)
       (*result x)
       pzero))))

(defn *char [c]
  (*sat (fn [x]
          (= x c))))

((*char \a) "abc")

(defn many []
  (fn []

    )
  )

(defn parse-int []
  (fn [s]
    (cond
      (= (count s) 0)
      []


      )
    )
  )

(re-find #"[0-9]+" "123 abc")


(defn parse-add []
  )

(defn my-parser []
  (fn [src]
    (let [[n1 src] (parse-int src)
          [add src] (parse-add src)
          [n2 src] (parse-int src)]
      [(+ n1 n2) src]
      )

    ))


((my-parser) program)
