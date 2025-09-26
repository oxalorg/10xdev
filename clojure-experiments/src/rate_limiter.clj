(ns rate-limiter
  (:import (java.time Instant Duration)))

;; (defprotocol RateLimiter
;;   (inc! [_])
;;   (dec! [_])
;;   )

;; There is a bucket which has `n` tokens in it
;; The bucket has a max capacity of `c`
;; `c = limit + burst`
;; forget about burst and limits for now

;; now comes a `rate` which is the rate at which we fill the bucket
;; `rate` = some unit of value to fill every ttl
;; we empty the bucket every
(defprotocol Bucket
  (current-tokens [_] "Get the current number of tokens in the bucket")
  (inc! [_] "Add one token to the bucket")
  (dec! [_] "Remove one token from the bucket"))

(defrecord InMemoryBucket [!agent]
  Bucket
  (current-tokens [_]
    @!agent)
  (inc! [_]
    (send !agent inc))
  (dec! [_]
    (send !agent dec)))

(defn can-process-request? [bucket]
  "Returns true if there are tokens available for processing"
  (pos? (current-tokens bucket)))

(comment
  (def my-agent (agent 0))
  (def my-bucket (InMemoryBucket. my-agent))
  (current-tokens my-bucket) ; => 0
  (inc! my-bucket)
  (inc! my-bucket)
  (await my-agent)
  (current-tokens my-bucket) ; => 2
  (dec! my-bucket)
  (await my-agent)
  (current-tokens my-bucket) ; => 1

  ;; Usage:
  (def rate-limiter (InMemoryBucket. (agent 5))) ; Start with 5 tokens
  (current-tokens rate-limiter) ; => 4

  (def !a (agent 0))
  (def bucket (InMemoryBucket. !a))

  (inc! bucket)
  (dec! bucket)
  (current-tokens bucket)

  )
