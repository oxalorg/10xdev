(ns sched
  (:import (java.time LocalDateTime))
  )

(def !jobs (atom clojure.lang.PersistentQueue/EMPTY))

(defn pending-jobs [jobs]
  (remove :finished jobs))

(defn now []
  (java.time.LocalDateTime/now))

;; CFS
(defn fact
  "Returns a factorial"
  [n]
  (loop [x 1
         total (bigint 1)]
    (if (> x n)
      total
      (recur (inc x)
             (* total x)))))

(defn cpu-intensive-task []
  (let [n (rand-int 100000)]
    (fact n)))

(defn schedule-job []
  (swap! !jobs conj {:arrived (now)
                     :exec-fn cpu-intensive-task}))

(defn scheduler! []
  (let [t-start (now)]
    (loop [pending-jobs @!jobs]
      (when (seq pending-jobs)
        ;; fifo algorithm
        (let [{:keys [exec-fn] :as jobs} (peek pending-jobs)
              output (exec-fn)
              t-end (now)]
          (println "Processed in " (java.time.Duration/between t-end t-start))
          (recur (swap! !jobs pop))
          )))))

(comment
  (for [i (range 5)]
    (schedule-job))

  @!jobs

  (scheduler!)

  (reset! !jobs nil)
  ,)
