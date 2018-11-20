;; gorilla-repl.fileformat = 1

;; **
;;; # ast
;;; 
;;; #### TODO
;;; 1. Readin data (Done)
;;; 2. Get input data & target data (Done)
;;; 3. crossover (Testing)
;;; 4. mutation (if error > C, flip bit?)
;;; 5. Error function
;;; 6. Simple GP
;;; 7. function to decide which crossover to use overtime
;;; 8. tournament->lexicase
;;; 
;;; 
;;; 
;;; 
;; **

;; @@
(ns ast
  (:require [gorilla-plot.core :as plot]
   			[propel.core :refer :all]
   			[clojure-csv.core :refer :all]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(require '[clojure.data.csv :as csv]
         '[clojure.java.io :as io])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(defn readin-data
  [file-name]
  (with-open [reader (io/reader file-name)]
  (doall
    (csv/read-csv reader))))
;c
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;ast/readin-data</span>","value":"#'ast/readin-data"}
;; <=

;; @@
(defn read-column [filename column-index]
  (with-open [reader (io/reader filename)]
    (let [data (csv/read-csv reader)]
      (doall
        (map #(nth % column-index) data)))))


;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;ast/read-column</span>","value":"#'ast/read-column"}
;; <=

;; @@
(type (readin-data "src/training_set_metadata.csv"))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-class'>clojure.lang.LazySeq</span>","value":"clojure.lang.LazySeq"}
;; <=

;; @@
;;(apply #(nth % 11) (readin-data "src/training_set_metadata.csv"))
;; @@

;; @@
;;( type 
;;	(read-column "src/training_set_metadata.csv" 11))

(defn get-target-data
  [file-name target-column]
  (rest 
    (apply vector 
           (read-column file-name target-column))))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;ast/get-target-data</span>","value":"#'ast/get-target-data"}
;; <=

;; @@
(def data-names ["object_id" "ra" "decl" "gal_l" "gal_b" "ddf" "hostgal_specz" "hostgal_photoz" "hostgal_photoz_err" "distmod" "mwebv" "target"])
(nth data-names 1)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-string'>&quot;ra&quot;</span>","value":"\"ra\""}
;; <=

;; @@
;; bind input data name to data
(map 
  #(def %1 
     (vec (get-target-data "src/training_set_metadata.csv" %2)))
  data-names
     (range 12))

(comment
;; v2
(doseq [n data-names number (range 12)] 
  (def n 
    (vec (get-target-data "src/training_set_metadata.csv" number)))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
ddf

;; @@

;; @@
;;brutal-force biding
(def ra (vec (get-target-data "src/training_set_metadata.csv" 1)))
(def decl (vec (get-target-data "src/training_set_metadata.csv" 2)))
(def gal_l (vec (get-target-data "src/training_set_metadata.csv" 3)))
(def gal_b (vec (get-target-data "src/training_set_metadata.csv" 4)))
(def ddf (vec (get-target-data "src/training_set_metadata.csv" 5)))
(def hostgal_specz (vec (get-target-data "src/training_set_metadata.csv" 6)))
(def hostgal_photoz (vec (get-target-data "src/training_set_metadata.csv" 7)))
(def hostgal_photoz_err (vec (get-target-data "src/training_set_metadata.csv" 8)))
(def distmod (vec (get-target-data "src/training_set_metadata.csv" 9)))
(def mwebv (vec (get-target-data "src/training_set_metadata.csv" 10)))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;ast/mwebv</span>","value":"#'ast/mwebv"}
;; <=

;; @@
(defn integer_absolute
  [state]
  (make-push-instruction state
                         #(max % (- %))
                         [:integer]
                         :integer))

(defn boolean_negative
  [state]
  (make-push-instruction state
                         #(neg? %)
                         [:integer]
                         :boolean))


(defn boolean_positive
  [state]
  (make-push-instruction state
                         #(pos? %)
                         [:integer]
                         :boolean))

(defn integer_sqrt
  [state]
  (make-push-instruction state
                         #(Math/sqrt %)
                         [:integer]
                         :integer))


;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;ast/integer_sqrt</span>","value":"#'ast/integer_sqrt"}
;; <=

;; @@
(defn tournament-selection-revised
  "Elements are sorted according to their erropr first and then the first half will be taken. After which 1/10 of them will be selected"
  [pop]
  (let [half-size (/ (count pop) 2)
        tournament-set (take half-size (apply min-key :total-error pop))
        tournament-size (/ (count tournament-set) 10)]
        (take tournament-size (shuffle pop))
    ))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;ast/tournament-selection-revised</span>","value":"#'ast/tournament-selection-revised"}
;; <=

;; @@
(defn uniform-crossover
  "Crosses over two individuals using uniform crossover. Pads shorter one."
  [plushy-a plushy-b]
  (let [shorter (min-key count plushy-a plushy-b)
        longer (if (counted? plushy-a);;choose a quicker way to set longer&shorter
                 (max-key count plushy-b plushy-a)
                 (if (= shorter plushy-a)
                   plushy-b
                   plushy-a))
        
        length-diff (- (count longer) (count shorter))
        shorter-padded (concat shorter (repeat length-diff :crossover-padding))]
    
    (remove #(= % :crossover-padding)
            (map #(if (< (rand) 0.5) %1 %2)
                 shorter-padded
                 longer))))

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;ast/uniform-crossover</span>","value":"#'ast/uniform-crossover"}
;; <=

;; @@
(defn multi-point-crossover-interleaving
  "Multi point crossover is a generalization of the one-point crossover wherein alternating segments are swapped to get new off-springs...
  take odd genomes, uniform sized
  a-1+b-2+a-3+b-4+...+a_left+b_left"
  
  [plushy-a plushy-b]
  (let [shorter (min-key count plushy-a plushy-b)
        longer (if (= shorter plushy-a)
                   plushy-b
                   plushy-a)
        length (count longer) ;;length of genes
        ;;at least 2 chunks'
        chunk-number (+ 2 (rand-int (dec length)))
        chunk-size (int (/ length chunk-number))
        length-diff (- (count longer) (count shorter))
        shorter-padded (concat shorter (repeat length-diff :crossover-padding))
        segmented-a (map vec (partition-all chunk-size plushy-a))
        segmented-b (map vec (partition-all chunk-size plushy-b))]  
    
    (loop [use-a (rand-nth [true false])
           a segmented-a
           b segmented-b
           result []]
        
      (if (empty? a)
        (remove #(= % :crossover-padding) result)
        (recur (not use-a)
               (rest a)
               (rest b)
               (concat result (if use-a
                              (first a) 
                              (first b)))))))) 



(defn multi-point-crossover-parallel
  "a1-b1-a3-b3-... or a-2-b2-a4-b4-..."
  [plushy-a plushy-b]
  (let [shorter (min-key count plushy-a plushy-b)
        longer (if (= shorter plushy-a)
                   plushy-b
                   plushy-a)
        length (count longer) ;;length of genes
        chunk-number (+ 1 (rand-int length))
        chunk-size (int (/ length chunk-number))
        length-diff (- (count longer) (count shorter))
        shorter-padded (concat shorter (repeat length-diff :crossover-padding))
        segmented-a (map vec (partition-all chunk-size plushy-a))
        segmented-b (map vec (partition-all chunk-size plushy-b))]
    
    (loop [start-at-0th (rand-nth [true false])
           a (if start-at-0th
               segmented-a
               (rest segmented-a))
           b (if start-at-0th
               segmented-b
               (rest segmented-b))
           result []]
        
      (if (empty? a)
        (remove #(= % :crossover-padding) result)
        (recur start-at-0th
               (rest (rest a))
               (rest (rest b))
               (concat result (first a) (first b))))))) 

   


;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;ast/multi-point-crossover-parallel</span>","value":"#'ast/multi-point-crossover-parallel"}
;; <=

;; @@
(comment
"Bit string mutation
The mutation of bit strings ensue through bit flips at random positions.

This mutation operator takes the chosen genome and inverts the bits (i.e. if the genome bit is 1, it is changed to 0 and vice versa).

Non-Uniform
The probability that amount of mutation will go to 0 with the next generation is increased by using non-uniform mutation operator. It keeps the population from stagnating in the early stages of the evolution. It tunes solution in later stages of evolution. This mutation operator can only be used for integer and float genes.

Uniform
This operator replaces the value of the chosen gene with a uniform random value selected between the user-specified upper and lower bounds for that gene. This mutation operator can only be used for integer and float genes.

Gaussian
This operator adds a unit Gaussian distributed random value to the chosen gene. If it falls outside of the user-specified lower or upper bounds for that gene, the new gene value is clipped. This mutation operator can only be used for integer and float genes.

Shrink
This operator adds a random number taken from a Gaussian distribution with mean equal to the original value of each decision variable characterizing the entry parent vector.")
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(defn bit-mutation
  "see definition above.bMutation rate [0 1)"
  [plushy mutation-rate]
  (map #(if (<= (rand) mutation-rate)
             (rand-nth instructions)
             %) 
       plushy))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;ast/bit-mutation</span>","value":"#'ast/bit-mutation"}
;; <=

;; @@

;; @@

;; @@

;; @@
