;; gorilla-repl.fileformat = 1

;; **
;;; # ast-test
;;; 
;; **

;; @@
(ns gp.propel-ast
  (:require [gorilla-plot.core :as plot]
   			[propel.core :refer :all]
            [gp.propel-ast :refer :all]
   			[clojure-csv.core :refer :all]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(def plushy-a [:string_+ :string_- :string_* :string_%])
(def plushy-b [:Ineger_+ :Integer_- :Integer_* :Integer_%])


(def chunk-size 1)
(def length (count plushy-a))
(def index (atom 0))



(defn multi-point-crossover-parallel
  "Multi point crossover is a generalization of the one-point crossover wherein alternating segments are swapped to get new off-springs...
  take odd genomes, uniform sized
  a-1+b-1+a-3+b-3+...+a_left+b_left"
  
  [plushy-a plushy-b]
  (let [shorter (min-key count plushy-a plushy-b)
        longer (if (counted? plushy-a)
                 (max-key count plushy-b plushy-a)
                 (if (= shorter plushy-a)
                   plushy-b
                   plushy-a))
        length (count longer) ;;length of genes
        point-number (+ 1 (rand-int (- length 2))) ;;how many cut points
        chunk-size (int (/ length (inc point-number))) ;; size per chunk
        index (atom 0) ;; pointer
        length-diff (- (count longer) (count shorter))
        shorter-padded (concat shorter (repeat length-diff :crossover-padding))]
    
     (remove #(= % :crossover-padding)
              (concat
                (while (< (+ chunk-size @index) length);;while no overflow
                  (do
                    ;;I think I have to apply this function to the arguments but apply didn't work..
                    #(do(
                       ;;take a chunk from both parent genes
                       (drop @index (take (+ (chunk-size @index)) %1))
                       (drop @index (take (+ (chunk-size @index)) %2))
                       ;;update index, skip next chunk for both parent genes
                       (swap! (+ @index (* 2 chunk-size)))))
                    plushy-a;;@1
                    plushy-b));;@2
                (drop index plushy-a);; concat with what's left in plushy-a
                (drop index plushy-b)))));;and plushy-b
(multi-point-crossover-parallel plushy-a plushy-b)
;; @@

;; @@
(defn multi-point-crossover-interleave
  "Multi point crossover is a generalization of the one-point crossover wherein alternating segments are swapped to get new off-springs...
  take odd genomes, uniform sized
  a-1+b-2+a-3+b-4+...+a_left+b_left"
  
  [plushy-a plushy-b]
  (let [shorter (min-key count plushy-a plushy-b)
        longer (if (counted? plushy-a)
                 (max-key count plushy-b plushy-a)
                 (if (= shorter plushy-a)
                   plushy-b
                   plushy-a))
        length (count longer) ;;length of genes
        point-number (+ 1 (rand-int (- length 2))) ;;how many cut points
        chunk-size (int (/ length (inc point-number))) ;; size per chunk
        index (atom 0) ;; pointer
        length-diff (- (count longer) (count shorter))
        shorter-padded (concat shorter (repeat length-diff :crossover-padding))]
    
     (remove #(= % :crossover-padding)
              (concat
                (while (< (+ chunk-size @index) length);;while no overflow
                  (do
                    ;;I think I have to apply this function to the arguments but apply didn't work..
                    #(do(
                       ;;take a chunk from the current parent gene
                       (drop @index (take (+ (chunk-size @index)) %))
                       ;;update index
                       (swap! (+ @index chunk-size))))
                    (if (even? (int (/ @index chunk-size)));;start from parent a
                      plushy-a
                      plushy-b)))
                (drop index plushy-a);; concat with what's left in plushy-a
                (drop index plushy-b)))));;and plushy-b

(multi-point-crossover-interleave plushy-a plushy-b)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;user/k-point-crossover-interleave</span>","value":"#'user/k-point-crossover-interleave"}
;; <=

;; @@

;; @@