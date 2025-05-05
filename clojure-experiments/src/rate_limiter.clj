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
  (get [_])
  (inc! [_])
  (dec! [_]))

(defrecord InMemoryBucket [!agent]
  Bucket
  (get [_]
    @!agent)
  (inc! [_]
    (send !agent inc))
  (dec! [_]
    (send !agent dec)))

(def !a (agent 0))
(def bucket (InMemoryBucket. !a))

(inc! bucket)
(dec! bucket)
(get bucket)
