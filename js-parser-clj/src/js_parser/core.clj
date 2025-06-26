(ns js-parser.core
  (:require [clojure.string :as str])
  (:import (java.lang Character)))

(def sample "
var size = 800;
function setup() {
  canvas.style.width = size + 'px';
  canvas.style.height = size + 'px';

  // Set actual size in memory (scaled to account for extra pixel density).
  var scale = window.devicePixelRatio; // Change to 1 on retina screens to see blurry canvas.
  canvas.width = size * scale;
  canvas.height = size * scale;

  // Normalize coordinate system to use css pixels.
  ctx.scale(scale, scale);
}

const drawCircle = (x, y, radius, color='black') => {
  ctx.save();
  ctx.beginPath();
  ctx.lineWidth = 5;
  ctx.strokeStyle = color;
  ctx.arc(x, y, radius, 0, 2 * Math.PI);
  ctx.stroke();
  ctx.restore();
}
")

(defn get-ctx [s]
  {:curr 0
   :tokens []
   :length (count s)
   :source (vec s)})

(defn peek [{:keys [curr length source tokens]}]
  (if (< curr length)
    (nth source curr)
    -1))

(defn step [{:keys [curr source tokens] :as ctx}]
  (update ctx :curr inc))

(defn letter? [c]
  (or (<= (int \a) (int c) (int \z))
      (<= (int \A) (int c) (int \Z))))

(defn digit? [c]
  (<= (int \0) (int c) (int \9)))

(defn blank? [c]
  (or (= c \space)
      (= c \tab)
      (= c \newline)))

(defn non-blank? [c]
  (not (blank? c)))

(defn identifier-char? [c]
  (or (letter? c)
      (digit? c)
      (= c \$)
      (= c \_)
      (= c \-)))

(defn push-token [ctx token]
  (update ctx :tokens conj token))

(defn consume-identifier [{:keys [curr source tokens] :as ctx}]
  (let [start curr]
    (loop [ctx* ctx
           s []]
      (let [c (peek ctx*)]
        (cond
          (identifier-char? c)
          (recur (-> ctx* step)
                 (conj s c))

          :else
          (-> ctx* (push-token {:kind :identifier
                                :start start
                                :end (dec (:curr ctx*))
                                :name (str/join "" s)})))))))

(defn parse-int [s]
  (Integer/parseInt s)
  #_(try
      (Integer/parseInt s)
      (catch Exception _
        nil)))

(defn consume-number [{:keys [curr source tokens] :as ctx}]
  (let [start curr]
    (loop [ctx* ctx
           s []]
      (let [c (peek ctx*)]
        (cond
          (digit? c)
          (recur (-> ctx* step)
                 (conj s c))

          :else
          (-> ctx* (push-token {:kind :number
                                :start start
                                :end (dec (:curr ctx*))
                                :value (parse-int (str/join "" s))})))))))

(defn tokenize [{:keys [curr tokens source] :as ctx}]
  (let [c (peek ctx)]
    (cond
      (blank? c)
      (recur (step ctx))

      (digit? c)
      (recur (-> ctx
                 consume-number))

      (identifier-char? c)
      (recur (-> ctx
                 consume-identifier))

      (= c \=)
      (recur (-> ctx
                 (push-token {:kind :equal})
                 step))

      (= c \;)
      (recur (-> ctx
                 (push-token {:kind :semicolon})
                 step))

      -1 ;; eof
      ctx

      :else
      ctx)))

(def sample-1 "var size = 800;")

(comment
  (consume-identifier (get-ctx sample-1))

  (tokenize (get-ctx sample-1))

  ,)
