(ns simple-compiler01
  (:require [clojure.test :as t :refer [deftest is are testing]])
  )

(def env (atom {"+" +
                "-" -
                "/" /
                "*" *}))

(defn runfn [[f & args]]
  (println :runfn f args (get @env (str f)))
  (let [f* (get @env (str f))]
    (apply f* args)))

(defmacro antilang [& body]
  (apply reverse body))

(macroexpand '(antilang
                (defn runfn [[f & args]]
                  (println :runfn f args (get @env (str f)))
                  (let [f* (get @env (str f))]
                    (apply f* args)))))

(defn pop-eval
  "Pop and apply func until you find :open"
  [stack]
  (println :pop-eval stack)
  (loop [stack stack
         args []]
    (if (= :open (peek stack))
      (cond-> (pop stack)
        (seq args) (conj (runfn (reverse args))))
      (recur (pop stack) (conj args (peek stack))))))

(defn parse [src]
  (loop [stack []
         src (seq src)]
    (let [c (first src)
          top (peek stack)]
      (println :c c)
      (cond
        (= c nil)
        stack

        (= c \space)
        (recur stack (rest src))

        (= c \()
        (recur (conj stack :open) (rest src))

        (= c \))
        (let [stack* (pop-eval stack)]
          (recur stack* (rest src)))

        (< (int \0) (int c) (int \9))
        (recur (conj stack (Integer/parseInt (str c))) (rest src))

        (not= c -1)
        (recur (conj stack c) (rest src))

        :else
        (throw (ex-info "error state, stack: " stack " src pending: " src))))))

(deftest arithmetic-test
  (are [program stack] (= (parse program) stack)
    "(+ 1 2)" [3]
    "(+ 1 2)" [3]
    "(+ 1 (+ 1 3))" [5]
    "(+ 1 (+ (+ 3 4) 5))" [13]
    "(+ 1 (+ (+ 3 4) 5)) (+ 1 2) ()" [13 3]
    "(* 1 (- (/ 8 4) 5)) (* 3 2) ()" [-3 6]))
