(ns json-parser
  (:require [clojure.string :as str]))

(declare parse)

(defn consume-until [src token]
  (loop [src src]
    (if (= (first src) token)
      (rest src)
      (recur (rest src)))))

(defn parse-str [src]
  (let [src (rest src) ;; drop first quote, todo check if it's a quote
        [key-seq src-seq] (split-with (comp not #{\"}) src)]
    (if (= (first src-seq) \")
      [(str/join "" key-seq) (rest src-seq)]
      (throw (ex-info "Invalid, no quote ending" {})))))

(defn parse-int [src]
  (let [[number-seq src-seq] (split-with #(<= (int \0) (int %) (int \9)) src)]
    [(->> number-seq (str/join "") #_#(Integer/parseInt %))
     src-seq]))

(comment
  (parse-str "abcd\": 123")
  (parse-int "1234567890}")
  ,)

(defn parse-obj [src]
  (loop [m {}]
    (let [[s src*] (parse-str (rest src))
          src* (consume-until src* \:)
          [v src*] (parse src*)]
      (recur (assoc m (keyword s) v) src*))
    )
  )


(defn parse [src]
  (loop [m {}
         src (seq src)]
    (println "[loop] " m src)
    (let [c (first src)]
      (cond
        (= c nil)
        m

        (= c \space)
        (recur m (rest src))

        (= c \{)
        (let []

          )
        (let [[s src*] (parse-str (rest src))
              src* (consume-until src* \:)
              [v src*] (parse src*)]
          (recur (assoc m (keyword s) v) src*))

        (= c \")
        (parse-str src)

        (< (int \0) (int c) (int \9))
        (let [[num src*] (parse-int src)]
          ;; check if not reached EOF
          [num src*])

        ;; (not= c -1)
        ;; (recur m (rest src))

        :else
        [-1 src]
        #_(throw (ex-info (str "error state, stack: " m " src pending: " src)))))))


(def src "{\"a\": 123, \"b\": \"hi\"}")

(parse src)
